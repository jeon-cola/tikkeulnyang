package com.c107.share.service;

import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.service.PaymentHistoryService;
import com.c107.s3.repository.S3Repository;
import com.c107.share.dto.ShareCommentDto;
import com.c107.share.dto.ShareLedgerResponseDto;
import com.c107.share.dto.ShareNotificationDto;
import com.c107.share.dto.SharePartnerDto;
import com.c107.share.entity.ShareEntity;
import com.c107.share.entity.ShareInteractionEntity;
import com.c107.share.entity.ShareNotificationEntity;
import com.c107.share.repository.ShareInteractionRepository;
import com.c107.share.repository.ShareNotificationRepository;
import com.c107.share.repository.ShareRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ShareInteractionRepository shareInteractionRepository;
    private final ShareNotificationRepository shareNotificationRepository;

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

            // 0: 지출이 없음, 1: 예산보다 적게 지출, 2: 예산 초과
            int emoji;
            if (dailySpent == 0) {
                emoji = 0; // 지출이 없음
            } else if (dailySpent > dailyBudget) {
                emoji = 2; // 예산 초과
            } else {
                emoji = 1; // 예산보다 적게 지출
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
                .linkExpire(LocalDateTime.now().plusDays(1))
                .status(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        shareRepository.save(shareEntity);

        return invitationLink;
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


    // 특정 날짜에 달린 댓글과 이모지 조회
    @Transactional(readOnly = true)
    public ShareCommentDto.DailyCommentsResponse getDailyComments(String dateStr, String email) {
        // 요청자 정보 조회
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        // 요청자가 포함된 공유 관계 찾기
        List<ShareEntity> shares = shareRepository.findByUserInActiveShare(requester.getUserId());

        List<ShareCommentDto.CommentResponse> allComments = new ArrayList<>();
        int likeCount = 0;
        int coolCount = 0;
        int fightingCount = 0;

        // 각 공유 관계에 대해 상호작용 조회
        for (ShareEntity share : shares) {
            // 요청자가 소유자이거나 공유된 사용자인 경우 처리
            if (share.getOwnerId().equals(requester.getUserId()) ||
                    (share.getSharedUserId() != null && share.getSharedUserId().equals(requester.getUserId()))) {

                // 해당 공유 관계의 해당 날짜 상호작용 조회
                List<ShareInteractionEntity> interactions = shareInteractionRepository
                        .findByShareIdAndTargetDateOrderByCreatedAtDesc(share.getShareId(), date);

                // 상호작용 정보를 DTO로 변환
                for (ShareInteractionEntity interaction : interactions) {
                    User commenter = userRepository.findById(interaction.getUserId())
                            .orElse(null);

                    String nickname = commenter != null ? commenter.getNickname() : "Unknown";
                    String profileImageUrl = commenter != null ? getProfileImageForUser(commenter.getUserId()) : defalutImage;

                    allComments.add(ShareCommentDto.CommentResponse.builder()
                            .commentId(interaction.getInteractionId().longValue())
                            .userId(interaction.getUserId())
                            .userNickname(nickname)
                            .profileImageUrl(profileImageUrl)
                            .comment(interaction.getCommentContent())
                            .emoji(interaction.getEmoji())
                            .createdAt(interaction.getCreatedAt())
                            .build());
                }

                // 이모지 개수 집계
                likeCount += shareInteractionRepository.countByShareIdAndTargetDateAndEmoji(share.getShareId(), date, 0);
                coolCount += shareInteractionRepository.countByShareIdAndTargetDateAndEmoji(share.getShareId(), date, 1);
                fightingCount += shareInteractionRepository.countByShareIdAndTargetDateAndEmoji(share.getShareId(), date, 2);
            }
        }

        // 최신 날짜순으로 정렬
        allComments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // 이모지 개수 DTO 생성
        ShareCommentDto.EmojiCounts emojiCounts = ShareCommentDto.EmojiCounts.builder()
                .like(likeCount)
                .cool(coolCount)
                .fighting(fightingCount)
                .build();

        // 최종 응답 생성
        return ShareCommentDto.DailyCommentsResponse.builder()
                .date(dateStr)
                .comments(allComments)
                .emojiCounts(emojiCounts)
                .build();
    }

    // 타인의 가계부에 댓글 및 이모지 등록
    @Transactional
    public ShareCommentDto.CommentResponse addComment(Long targetUserId, String dateStr, ShareCommentDto.CommentRequest request, String email) {
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("요청자 정보를 찾을 수 없습니다."));

        // 대상 사용자 정보 조회
        User targetUser = userRepository.findById(targetUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));

        // 공유 중인 관계 확인 (요청자와 targetUserId 간의 공유 관계)
        ShareEntity shared = shareRepository.findActiveShareByUsers(requester.getUserId(), targetUserId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("공유 중인 관계가 없습니다."));

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        // 이미 댓글/이모지를 남겼는지 확인
        boolean alreadyCommented = shareInteractionRepository.existsByUserIdAndShareIdAndTargetDate(
                requester.getUserId(), shared.getShareId(), date);

        if (alreadyCommented) {
            throw new IllegalArgumentException("이미 해당 날짜에 댓글 또는 이모지를 등록했습니다.");
        }

        // 이모지 값 검증
        if (request.getEmoji() != null && (request.getEmoji() < 0 || request.getEmoji() > 2)) {
            throw new IllegalArgumentException("유효하지 않은 이모지 값입니다. 0, 1, 2 중 하나여야 합니다.");
        }

        // 댓글 및 이모지 저장
        LocalDateTime now = LocalDateTime.now();
//        ShareInteractionEntity interaction = ShareInteractionEntity.builder()
//                .shareId(shared.getShareId())
//                .userId(requester.getUserId())
//                .targetDate(date)
//                .commentContent(request.getComment())
//                .emoji(request.getEmoji())
//                .createdAt(now)
//                .updatedAt(now)
//                .build();
        // 댓글 및 이모지 저장
        ShareInteractionEntity interaction = ShareInteractionEntity.builder()
                .shareId(shared.getShareId())
                .userId(requester.getUserId())
                .targetDate(date)
                .commentContent(request.getComment())
                .emoji(request.getEmoji())
                .createdAt(now)
                .updatedAt(now)
                .build();

        ShareInteractionEntity savedInteraction = shareInteractionRepository.save(interaction);

        // 알림 생성 (대상 사용자에게)
        createNotification(
                shared.getShareId(),
                targetUser.getUserId(),
                savedInteraction.getInteractionId(),
                date);

        // 프로필 이미지 URL 가져오기
        String profileImageUrl = getProfileImageForUser(requester.getUserId());

        return ShareCommentDto.CommentResponse.builder()
                .commentId(savedInteraction.getInteractionId().longValue())
                .userId(requester.getUserId())
                .userNickname(requester.getNickname())
                .profileImageUrl(profileImageUrl)
                .comment(savedInteraction.getCommentContent())
                .emoji(savedInteraction.getEmoji())
                .createdAt(savedInteraction.getCreatedAt())
                .build();

    }

    // 알림이 있는 날짜 목록 월별 조회(빨간 점)
    @Transactional(readOnly = true)
    public ShareNotificationDto.NotificationDatesResponse getNotificationDates(String email, Integer year, Integer month) {

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        List<String> notificationDates = shareNotificationRepository
                .findDistinctNotificationDatesByUserIdAndYearAndMonth(
                        requester.getUserId(), targetYear, targetMonth);

        return ShareNotificationDto.NotificationDatesResponse.builder()
                .dates(notificationDates)
                .build();
    }

    // 특정 날짜의 모든 알림 읽음 처리

    @Transactional
    public void markNotificationsAsReadByDate(String dateStr, String email) {

        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. YYYY-MM-DD 형식이어야 합니다.");
        }

        // 해당 날짜의 모든 알림 읽음 처리
        int updatedCount = shareNotificationRepository.markAllNotificationsAsReadByDate(
                requester.getUserId(), date);

        log.info("사용자 {}의 {} 날짜 알림 {} 개가 읽음 처리되었습니다.", requester.getUserId(), dateStr, updatedCount);
    }


    // 상호작용 발생 시 알림 생성
    private void createNotification(Integer shareId, Integer targetUserId, Integer interactionId, LocalDate targetDate) {

        LocalDateTime now = LocalDateTime.now();

        ShareNotificationEntity notification = ShareNotificationEntity.builder()
                .shareId(shareId)
                .userId(targetUserId)
                .interactionId(interactionId)
                .targetDate(targetDate)
                .isRead(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        shareNotificationRepository.save(notification);
    }

    @Transactional
    public String acceptInvitation(String token, String invitedEmail) {
        // 1) 초대 링크로 ShareEntity 찾기
        ShareEntity shareEntity = shareRepository.findByInvitationLinkEndingWith(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        // 2) 초대 링크 만료 여부 확인
        if (shareEntity.getLinkExpire().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("초대 링크가 만료되었습니다.");
        }

        // 3) 초대받은 사용자 조회
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new IllegalArgumentException("초대받은 사용자를 찾을 수 없습니다."));

        // 3-1) **자기 자신 초대 불가** (ownerId == 초대받은 userId)
        if (shareEntity.getOwnerId().equals(invitedUser.getUserId())) {
            throw new IllegalArgumentException("자기 자신에게는 초대할 수 없습니다.");
        }

        // 4) 이미 이 ShareEntity가 수락된 상태인지 확인
        if (shareEntity.getSharedUserId() != null) {
            throw new IllegalArgumentException("이미 초대가 수락된 링크입니다.");
        }

        // 4-1) **이미 두 사용자간 공유 관계(status=1)가 있는지 확인**
        boolean existsActive = shareRepository.existsActiveShareBetween(
                shareEntity.getOwnerId(), invitedUser.getUserId());
        if (existsActive) {
            throw new IllegalArgumentException("이미 공유 관계가 맺어진 사용자입니다.");
        }

        // 5) 초대 수락 처리
        shareEntity.setSharedUserId(invitedUser.getUserId());
        shareEntity.setStatus(1); // 공유 중
        shareRepository.save(shareEntity);

        return "초대가 수락되었습니다.";
    }


}