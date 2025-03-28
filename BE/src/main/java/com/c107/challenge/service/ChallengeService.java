package com.c107.challenge.service;

import com.c107.accounts.entity.ServiceTransaction;
import com.c107.accounts.repository.AccountTransactionRepository;
import com.c107.accounts.service.AccountService;
import com.c107.challenge.dto.ChallengeDetailResponseDto;
import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.dto.PastChallengeResponseDto;
import com.c107.challenge.entity.ChallengeEntity;
import com.c107.challenge.entity.UserChallengeEntity;
import com.c107.challenge.repository.ChallengeRepository;
import com.c107.challenge.repository.UserChallengeRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.s3.entity.S3Entity;
import com.c107.s3.repository.S3Repository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    private static final String DEFAULT_THUMBNAIL_URL = "https://my-catcat-bucket.s3.us-east-2.amazonaws.com/default/CloseIcon.png";
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    // AccountService 주입 (서비스 계좌 ID를 가져오기 위해)
    private final AccountService accountService;
    private final S3Repository s3Repository;

    // 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @Transactional
    public ChallengeResponseDto createChallenge(CreateChallengeRequest request) {
        String createdBy = getAuthenticatedUserEmail();
        boolean isAdmin = isAdmin();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestStartTime = now.toLocalDate().plusDays(1).atStartOfDay();
        if (request.getStartDate().isBefore(earliestStartTime.toLocalDate())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED,
                    "챌린지는 등록 후 최소 하루 뒤 자정(00:00)부터 시작할 수 있습니다.");
        }

        ChallengeEntity entity = ChallengeEntity.builder()
                .challengeName(request.getChallengeName())
                .challengeType(isAdmin ? "공식챌린지" : "유저챌린지")
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .createdBy(createdBy)
                .maxParticipants(request.getMaxParticipants())
                .limitAmount(request.getLimitAmount())
                .publicFlag(true)
                .activeFlag(false)
                .challengeCategory(request.getChallengeCategory())
                .createdAt(now)
                .updatedAt(now)
                .currentParticipants(1)
                .deleted(false)
                .build();

        ChallengeEntity saved = challengeRepository.save(entity);
        return mapToDto(saved);
    }

    // 챌린지 삭제 (시작 전인 경우만 삭제 가능)
    @Transactional
    public void deleteChallenge(Integer challengeId) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        String loggedInUser = getAuthenticatedUserEmail();
        boolean isAdmin = isAdmin();

        if (challenge.getActiveFlag() || !LocalDate.now().isBefore(challenge.getStartDate())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "시작된 챌린지는 삭제할 수 없습니다.");
        }

        if (!isAdmin && !challenge.getCreatedBy().equals(loggedInUser)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "삭제 권한이 없습니다.");
        }

        challenge.setDeleted(true);
        challengeRepository.save(challenge);
    }

    // 챌린지 수정은 항상 불가능
    @Transactional
    public ChallengeResponseDto updateChallenge(Integer challengeId, CreateChallengeRequest request) {
        throw new CustomException(ErrorCode.UNAUTHORIZED, "챌린지는 수정할 수 없습니다.");
    }

    public Page<ChallengeResponseDto> getOfficialChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository.findByChallengeTypeAndDeleted("공식챌린지", false, PageRequest.of(page, size));
        return challenges.map(this::mapToDto);
    }

    public Page<ChallengeResponseDto> getUserChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository.findByChallengeTypeAndDeleted("유저챌린지", false, PageRequest.of(page, size));
        return challenges.map(this::mapToDto);
    }

    // 인증 정보에서 로그인한 사용자 이메일 조회 (UserDetails 혹은 String으로 처리)
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            String username = (String) principal;
            if ("anonymousUser".equals(username)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
            }
            return username;
        }
        throw new CustomException(ErrorCode.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다.");
    }

    // 로그인한 User 객체 조회
    private User getAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private ChallengeEntity findChallengeById(Integer challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "챌린지를 찾을 수 없습니다."));
    }

    public ChallengeResponseDto getChallengeById(Integer challengeId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "챌린지를 찾을 수 없습니다."));
        return mapToDto(challenge);
    }

    @PostConstruct
    public void initializeChallenges() {
        activatePendingChallenges();
        logger.info("✅ 서버 시작 시, 활성화되지 않은 챌린지 확인 완료!");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void activateChallenges() {
        activatePendingChallenges();
        logger.info("✅ 0시 0분에 챌린지 활성화 완료!");
    }

    private void activatePendingChallenges() {
        LocalDate today = LocalDate.now();
        List<ChallengeEntity> challenges = challengeRepository.findByStartDateBeforeAndActiveFlagFalse(today);
        for (ChallengeEntity challenge : challenges) {
            challenge.setActiveFlag(true);
            challengeRepository.save(challenge);
            logger.debug("챌린지 활성화됨: ID = {}, Name = {}", challenge.getChallengeId(), challenge.getChallengeName());
        }
    }

    private ChallengeResponseDto mapToDto(ChallengeEntity entity) {

        String thumbnailUrl = s3Repository
                .findTopByUsageTypeAndUsageIdOrderByCreatedAtDesc("CHALLENGE", entity.getChallengeId())
                .map(S3Entity::getUrl)
                .orElse(DEFAULT_THUMBNAIL_URL);

        return ChallengeResponseDto.builder()
                .challengeId(entity.getChallengeId())
                .challengeName(entity.getChallengeName())
                .challengeType(entity.getChallengeType())
                .targetAmount(entity.getTargetAmount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .maxParticipants(entity.getMaxParticipants())
                .activeFlag(entity.getActiveFlag())
                .challengeCategory(entity.getChallengeCategory())
                .createdAt(entity.getCreatedAt())
                .limitAmount(entity.getLimitAmount())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    // ------------------ 챌린지 참여/취소 및 결과 처리 ------------------

    /**
     * 챌린지 참여
     * 로그인한 사용자가 참여할 챌린지가 아직 시작 전이면,
     * 챌린지의 targetAmount만큼 예치금이 차감되고 참여 기록과 거래내역이 생성됩니다.
     */
    @Transactional
    public void joinChallenge(Integer challengeId) {
        User user = getAuthenticatedUser();
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "챌린지를 찾을 수 없습니다."));

        if (!challenge.getStartDate().isAfter(LocalDate.now())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "이미 시작된 챌린지에는 참여할 수 없습니다.");
        }

        if (userChallengeRepository.findByUserIdAndChallenge_ChallengeIdAndStatus(user.getUserId(), challengeId, "진행중").isPresent()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "이미 참여하셨습니다.");
        }

        int depositAmount = challenge.getTargetAmount();
        if (user.getDeposit() < depositAmount) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "예치금이 부족합니다.");
        }

        user.setDeposit(user.getDeposit() - depositAmount);
        userRepository.save(user);
        logger.info("사용자 예치금 차감 완료: 차감액={}, 남은 예치금={}", depositAmount, user.getDeposit());

        UserChallengeEntity participation = UserChallengeEntity.builder()
                .challenge(challenge)
                .challengeName(challenge.getChallengeName())
                .userId(user.getUserId())
                .depositAmount(depositAmount)
                .status("진행중")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .spendAmount(0)
                .build();
        userChallengeRepository.save(participation);
        logger.info("챌린지 참여 기록 생성됨: {}", participation);

        // 거래내역 기록 (챌린지 참여) - AccountService의 public getServiceAccountId() 사용
        ServiceTransaction joinTx = ServiceTransaction.builder()
                .accountId(accountService.getServiceAccountId())
                .userId(user.getUserId())
                .transactionDate(LocalDateTime.now())
                .category("CHALLENGE_JOIN")
                .transactionType("WITHDRAW")
                .transactionBalance(depositAmount)
                .transactionAfterBalance(user.getDeposit())
                .description("챌린지 참여: " + challenge.getChallengeName() + " 참여로 예치금 차감")
                .build();
        accountTransactionRepository.save(joinTx);
        logger.info("챌린지 참여 거래내역 기록됨: {}", joinTx);
    }

    /**
     * 챌린지 참여 취소
     * 챌린지 시작 전인 경우에만 취소 가능하며, 차감된 예치금을 환불하고 거래내역을 기록합니다.
     */
    @Transactional
    public void cancelChallengeParticipation(Integer challengeId) {
        User user = getAuthenticatedUser();
        UserChallengeEntity participation = userChallengeRepository
                .findByUserIdAndChallenge_ChallengeIdAndStatus(user.getUserId(), challengeId, "진행중")
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "진행 중인 참여 기록을 찾을 수 없습니다."));

        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "챌린지를 찾을 수 없습니다."));
        if (!challenge.getStartDate().isAfter(LocalDate.now())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지가 이미 시작되어 참여 취소할 수 없습니다.");
        }

        int refundAmount = participation.getDepositAmount();
        user.setDeposit(user.getDeposit() + refundAmount);
        userRepository.save(user);
        logger.info("사용자 예치금 환불 완료: 환불액={}, 업데이트 후 예치금={}", refundAmount, user.getDeposit());

        participation.setStatus("취소");
        participation.setUpdatedAt(LocalDateTime.now());
        userChallengeRepository.save(participation);
        logger.info("챌린지 참여 취소 완료: {}", participation);

        // 거래내역 기록 (챌린지 참여 취소 환불)
        ServiceTransaction cancelTx = ServiceTransaction.builder()
                .accountId(accountService.getServiceAccountId())
                .userId(user.getUserId())
                .transactionDate(LocalDateTime.now())
                .category("CHALLENGE_CANCEL_REFUND")
                .transactionType("REFUND")
                .transactionBalance(refundAmount)
                .transactionAfterBalance(user.getDeposit())
                .description("챌린지 참여 취소: 예치금 환불")
                .build();
        accountTransactionRepository.save(cancelTx);
        logger.info("챌린지 취소 환불 거래내역 기록됨: {}", cancelTx);
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponseDto> getParticipatedChallenges() {
        User user = getAuthenticatedUser();
        List<UserChallengeEntity> participations = userChallengeRepository.findByUserIdAndStatus(user.getUserId(), "진행중");
        return participations.stream()
                .map(participation -> mapToDto(participation.getChallenge()))
                .collect(Collectors.toList());
    }

    /**
     * 챌린지 상세조회
     * - 챌린지 기본 정보
     * - 참가자 수
     * - 각 참가자의 과거 챌린지 성공률을 계산하여 아래 구간별 분포 산출:
     *      100~85%, 84~50%, 49~25%, 24~0%
     * - 전체 참가자의 평균 성공률 산출
     */
    @Transactional(readOnly = true)
    public ChallengeDetailResponseDto getChallengeDetail(Integer challengeId) {
        // 챌린지 기본 정보 조회
        ChallengeEntity challenge = findChallengeById(challengeId);
        // 현재 이 챌린지에 참가한(참가 상태가 "진행중") 모든 참가자 조회
        List<UserChallengeEntity> participants = userChallengeRepository.findByChallenge_ChallengeIdAndStatus(challengeId, "진행중");

        // 각 참가자의 과거 챌린지 참여 내역(진행중 제외)을 통해 성공률 계산
        double totalSuccessRateSum = 0.0;
        int count = 0;
        int bucket1 = 0; // 100 ~ 85%
        int bucket2 = 0; // 84 ~ 50%
        int bucket3 = 0; // 49 ~ 25%
        int bucket4 = 0; // 24 ~ 0%

        for (UserChallengeEntity participation : participants) {
            Integer userId = participation.getUserId();
            // 해당 사용자의 과거 참여 내역 조회(진행중 제외)
            List<UserChallengeEntity> history = userChallengeRepository.findByUserIdAndStatusNot(userId, "진행중");
            double successRate = 0.0;
            if (!history.isEmpty()) {
                long successCount = history.stream()
                        .filter(h -> "성공".equals(h.getStatus()))
                        .count();
                long total = history.size();
                successRate = ((double) successCount / total) * 100;
            }
            totalSuccessRateSum += successRate;
            count++;
            if (successRate >= 85) {
                bucket1++;
            } else if (successRate >= 50) {
                bucket2++;
            } else if (successRate >= 25) {
                bucket3++;
            } else {
                bucket4++;
            }
        }
        double averageSuccessRate = count > 0 ? totalSuccessRateSum / count : 0.0;

        // 기본 챌린지 DTO로 변환
        ChallengeResponseDto challengeDto = mapToDto(challenge);
        return ChallengeDetailResponseDto.builder()
                .challenge(challengeDto)
                .participantCount(participants.size())
                .bucket100to85(bucket1)
                .bucket84to50(bucket2)
                .bucket49to25(bucket3)
                .bucket24to0(bucket4)
                .averageSuccessRate(averageSuccessRate)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PastChallengeResponseDto> getPastParticipatedChallenges() {
        User user = getAuthenticatedUser();
        // "진행중"과 "취소" 상태를 제외한 참여 내역 조회
        List<UserChallengeEntity> participations = userChallengeRepository.findByUserIdAndStatusNot(user.getUserId(), "진행중");
        LocalDate today = LocalDate.now();
        return participations.stream()
                // 챌린지 종료일이 오늘 이전이고, 상태가 "취소"가 아닌 경우
                .filter(participation -> participation.getChallenge().getEndDate().isBefore(today)
                        && !participation.getStatus().equals("취소"))
                .map(participation -> {
                    ChallengeEntity challenge = participation.getChallenge();
                    return PastChallengeResponseDto.builder()
                            .challengeId(challenge.getChallengeId())
                            .challengeName(challenge.getChallengeName())
                            .challengeType(challenge.getChallengeType())
                            .targetAmount(challenge.getTargetAmount())
                            .startDate(challenge.getStartDate())
                            .endDate(challenge.getEndDate())
                            .description(challenge.getDescription())
                            .createdBy(challenge.getCreatedBy())
                            .maxParticipants(challenge.getMaxParticipants())
                            .activeFlag(challenge.getActiveFlag())
                            .challengeCategory(challenge.getChallengeCategory())
                            .createdAt(challenge.getCreatedAt())
                            .limitAmount(challenge.getLimitAmount())
                            // 참여 결과 상태 (예: "성공" 또는 "실패")
                            .participationStatus(participation.getStatus())
                            .thumbnailUrl(mapToDto(challenge).getThumbnailUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 챌린지 종료 후, 성공한 참여자에게 환불 처리 (예: 총 예치금 풀을 성공자에게 나눠줌)
    @Transactional
    public void settleChallenge(Integer challengeId) {
        // 챌린지가 종료되었는지 확인
        ChallengeEntity challenge = findChallengeById(challengeId);
        if (!challenge.getEndDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지가 아직 종료되지 않았습니다.");
        }
        // 해당 챌린지의 모든 참여 내역(상태 관계없이)
        List<UserChallengeEntity> allParticipants = userChallengeRepository.findByChallenge_ChallengeId(challengeId);
        if (allParticipants.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "참여 기록이 없습니다.");
        }
        // 성공한 참여자 선별 (상태가 "성공")
        List<UserChallengeEntity> successfulParticipants = allParticipants.stream()
                .filter(p -> "성공".equals(p.getStatus()))
                .collect(Collectors.toList());
        if (successfulParticipants.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "성공한 참여자가 없어 환불할 수 없습니다.");
        }
        // 총 예치금 풀 계산
        int totalPool = allParticipants.stream()
                .mapToInt(UserChallengeEntity::getDepositAmount)
                .sum();
        // 성공자 1인당 환불 금액
        int refundPerParticipant = totalPool / successfulParticipants.size();

        // 각 성공자에게 환불 처리
        for (UserChallengeEntity participation : successfulParticipants) {
            User user = userRepository.findById(participation.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "참여자의 사용자 정보를 찾을 수 없습니다."));
            user.setDeposit(user.getDeposit() + refundPerParticipant);
            userRepository.save(user);
            // 환불 거래내역 기록
            ServiceTransaction refundTx = ServiceTransaction.builder()
                    .accountId(accountService.getServiceAccountId())
                    .userId(user.getUserId())
                    .transactionDate(LocalDateTime.now())
                    .category("CHALLENGE_SETTLE_REFUND")
                    .transactionType("REFUND")
                    .transactionBalance(refundPerParticipant)
                    .transactionAfterBalance(user.getDeposit())
                    .description("챌린지 [" + challenge.getChallengeName() + "] 성공 환불")
                    .build();
            accountTransactionRepository.save(refundTx);
            logger.info("환불 거래내역 기록됨: {}", refundTx);
        }
    }


}
