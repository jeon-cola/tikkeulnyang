package com.c107.share.service;

import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.service.PaymentHistoryService;
import com.c107.s3.repository.S3Repository;
import com.c107.share.dto.InviteRequestDto;
import com.c107.share.dto.ShareLedgerResponseDto;
import com.c107.share.dto.SharePartnerDto;
import com.c107.share.entity.ShareEntity;
import com.c107.share.repository.ShareRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShareService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final ShareRepository shareRepository;
    private final S3Repository s3Repository;

    @Value("${app.base.url}")
    private String url;

    @Value("${default.profile.image.url}")
    private String defalutImage;


    @Transactional(readOnly = true)
    public ShareLedgerResponseDto getMyLedger(String email, Integer year, Integer month) {
        // 사용자 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        // 현재 날짜 정보로 기본값 설정
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

        // 1. 예산 정보 조회
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<BudgetEntity> budgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        int totalBudget = budgets.stream()
                .mapToInt(budget -> budget.getAmount() != null ? budget.getAmount() : 0)
                .sum();

        // 2. 소비 내역 조회
        PaymentHistoryResponseDto paymentHistory = paymentHistoryService.getConsumptionCalendar(
                email, targetYear, targetMonth, "personal");

        int totalIncome = paymentHistory.getTotalIncome();
        int totalSpent = paymentHistory.getTotalSpent();

        // 3. 일별 예산 계산 (월 예산을 일별로 분배)
        int daysInMonth = yearMonth.lengthOfMonth();
        double dailyBudget = totalBudget / (double) daysInMonth;

        // 4. 일별 데이터 생성
        List<ShareLedgerResponseDto.DailyData> dailyDataList = new ArrayList<>();

        // 일별 실제 지출 합계 계산
        Map<String, Integer> dailySpentMap = new HashMap<>();

        if (paymentHistory.getData() != null) {
            for (PaymentHistoryResponseDto.DayData dayData : paymentHistory.getData()) {
                dailySpentMap.put(dayData.getDate(), dayData.getExpense());
            }
        }

        // 달력 데이터 생성
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(targetYear, targetMonth, day);
            String dateStr = date.format(DateTimeFormatter.ISO_DATE);

            int dailySpent = dailySpentMap.getOrDefault(dateStr, 0);

            // 이모지 결정 (0: 예산 미만, 1: 예산과 같음, 2: 예산 초과)
            int emoji;
            if (Math.abs(dailySpent - dailyBudget) < 0.01) {
                emoji = 1; // 예산과 동일
            } else if (dailySpent > dailyBudget) {
                emoji = 2; // 예산 초과
            } else {
                emoji = 0; // 예산 미만
            }

            ShareLedgerResponseDto.DailyData dailyData = ShareLedgerResponseDto.DailyData.builder()
                    .date(dateStr)
                    .emoji(emoji)
                    .build();
            dailyDataList.add(dailyData);
        }

        // 응답 생성
        return ShareLedgerResponseDto.builder()
                .year(targetYear)
                .month(targetMonth)
                .totalIncome(totalIncome)
                .totalSpent(totalSpent)
                .totalBudget(totalBudget)
                .data(dailyDataList)
                .build();
    }

    public String generateInvitationLink(String email) {
        String token = UUID.randomUUID().toString();
        String invitationLink = url + "/" + token;

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가계부 소유자를 찾을 수 없습니다."));

        ShareEntity shareEntity = ShareEntity.builder()
                .ownerId(owner.getUserId())
                .invitationLink(invitationLink)
                .linkExpire(LocalDateTime.now().plusDays(7))
                .status(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        shareRepository.save(shareEntity);

        return invitationLink;
    }



    // 초대 수락 처리 메서드
    @Transactional
    public String acceptInvitation(String token, String invitedEmail) {
        // invitationLink의 마지막 부분(token)으로 ShareEntity를 조회
        ShareEntity shareEntity = shareRepository.findByInvitationLinkEndingWith(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        // 만료 여부 확인
        if (shareEntity.getLinkExpire().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("초대 링크가 만료되었습니다.");
        }

        // 초대받은 사용자 조회
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new IllegalArgumentException("초대받은 사용자를 찾을 수 없습니다."));

        // 이미 수락된 경우 중복 수락 방지
        if (shareEntity.getSharedUserId() != null) {
            throw new IllegalArgumentException("이미 초대가 수락되었습니다.");
        }

        // 초대 수락 처리: sharedUserId 업데이트, 상태를 1(공유 중)으로 변경
        shareEntity.setSharedUserId(invitedUser.getUserId());
        shareEntity.setStatus(1);
        shareRepository.save(shareEntity);

        return "초대가 수락되었습니다.";
    }

    @Transactional(readOnly = true)
    public ShareLedgerResponseDto getSharedLedger(String token, String requesterEmail, Integer year, Integer month) {
        // 초대 링크 토큰으로 ShareEntity 조회
        ShareEntity shareEntity = shareRepository.findByInvitationLinkEndingWith(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        // 요청한 사용자 정보 조회
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 요청한 사용자가 가계부 소유자(owner) 또는 초대받은 사용자(sharedUser)여야 함
        if (!requester.getUserId().equals(shareEntity.getOwnerId()) &&
                !requester.getUserId().equals(shareEntity.getSharedUserId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        // 가계부 소유자 정보 조회
        User owner = userRepository.findById(shareEntity.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("가계부 소유자를 찾을 수 없습니다."));

        // 원래 가계부 데이터 조회
        ShareLedgerResponseDto baseDto = getMyLedger(owner.getEmail(), year, month);

        // 공유 응답 DTO에 추가 정보 포함시켜 새로 빌드
        return ShareLedgerResponseDto.builder()
                .year(baseDto.getYear())
                .month(baseDto.getMonth())
                .totalIncome(baseDto.getTotalIncome())
                .totalSpent(baseDto.getTotalSpent())
                .totalBudget(baseDto.getTotalBudget())
                .data(baseDto.getData())
                .ownerNickname(owner.getNickname())
                .ownerEmail(owner.getEmail())
                .build();
    }


    public List<SharePartnerDto> getMyPartners(String email) {
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Integer myId = me.getUserId();

        // 내가 owner이거나 shared_user인 모든 공유 관계 중 status=1 (공유 중)인 것들
        List<ShareEntity> sharedRelations = shareRepository.findByUserInActiveShare(myId);

        return sharedRelations.stream().map(share -> {
            User partner;

            if (share.getOwnerId().equals(myId)) {
                partner = userRepository.findById(share.getSharedUserId()).orElse(null);
            } else {
                partner = userRepository.findById(share.getOwnerId()).orElse(null);
            }

            if (partner == null) return null;

            // 프로필 이미지 URL은 S3 연동된 이미지 테이블에서 가져오거나, User 엔티티에 있을 수도 있음
            String profileImageUrl = getProfileImageForUser(partner.getUserId());

            return SharePartnerDto.builder()
                    .userId(partner.getUserId().longValue())
                    .nickname(partner.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .build();
        }).filter(Objects::nonNull).toList();
    }

    public String getProfileImageForUser(Integer userId) {
        return s3Repository.findTopByUsageTypeAndUsageIdOrderByCreatedAtDesc("PROFILE", userId)
                .map(image -> image.getUrl())
                .orElse(defalutImage);
    }

    @Transactional
    public void unsharePartner(String myEmail, Long partnerUserId) {
        // 내 정보 조회
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Integer myId = me.getUserId();

        // 공유 중인 관계 찾기 (내가 소유자이거나 초대받은 사용자일 수 있음)
        ShareEntity share = shareRepository.findActiveShareByUsers(myId, partnerUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("공유 중인 관계가 없습니다."));

        // 공유 해제
        share.setStatus(2); // 2 = 공유 해제 상태
        shareRepository.save(share);
    }

    // 매일 새벽 3시에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredInvitationLinks() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = shareRepository.deleteByStatusAndLinkExpireBefore(0, now);
        log.info("[스케줄러] 만료된 초대 링크 {}개 삭제됨", deletedCount);
    }

    @Transactional(readOnly = true)
    public ShareLedgerResponseDto getSharedLedgerByUserId(Long targetUserId, String requesterEmail, Integer year, Integer month) {
        // 요청자 정보 조회
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보를 찾을 수 없습니다."));

        // 공유 중인 관계 확인 (요청자와 targetUserId 간의 공유 관계)
        ShareEntity shared = shareRepository.findActiveShareByUsers(requester.getUserId(), targetUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("공유 중인 관계가 없습니다."));

        // targetUserId의 데이터를 조회하도록 변경
        ShareLedgerResponseDto baseDto = getMyLedgerByUserId(targetUserId.intValue(), year, month);

        // targetUserId 정보를 조회 (닉네임, 이메일 등)
        User targetUser = userRepository.findById(targetUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 조회한 데이터를 기반으로 응답 DTO 생성
        return ShareLedgerResponseDto.builder()
                .year(baseDto.getYear())
                .month(baseDto.getMonth())
                .totalIncome(baseDto.getTotalIncome())
                .totalSpent(baseDto.getTotalSpent())
                .totalBudget(baseDto.getTotalBudget())
                .data(baseDto.getData())
                .ownerNickname(targetUser.getNickname())
                .ownerEmail(targetUser.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public ShareLedgerResponseDto getMyLedgerByUserId(Integer userId, Integer year, Integer month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        return getMyLedger(user.getEmail(), year, month);
    }

    // 상대방의 일별 가계부 상세내역 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getPartnerDailyLedger(Long targetUserId, String date, String requesterEmail) {
        // 요청자 정보 조회
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보를 찾을 수 없습니다."));

        // 공유 중인 관계 확인 (요청자와 targetUserId 간의 공유 관계)
        ShareEntity shared = shareRepository.findActiveShareByUsers(requester.getUserId(), targetUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("공유 중인 관계가 없습니다."));

        // targetUserId 정보를 조회 (닉네임, 이메일 등)
        User targetUser = userRepository.findById(targetUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 날짜 형식 검증
        try {
            LocalDate.parse(date); // YYYY-MM-DD 형식 검증
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        // PaymentHistoryService의 일별 소비 내역 조회 메서드 활용
        Map<String, Object> dailyData = paymentHistoryService.getDailyConsumption(targetUser.getEmail(), date);

        // 응답 데이터에 상대방 정보 추가
        dailyData.put("ownerNickname", targetUser.getNickname());
        dailyData.put("ownerEmail", targetUser.getEmail());

        return dailyData;
    }


}