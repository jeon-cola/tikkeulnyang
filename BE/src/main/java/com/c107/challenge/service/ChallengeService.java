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
import com.c107.paymenthistory.entity.CategoryEntity;
import com.c107.paymenthistory.repository.CategoryRepository;
import com.c107.s3.entity.S3Entity;
import com.c107.s3.repository.S3Repository;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private final AccountService accountService;
    private final S3Repository s3Repository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    // 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @Transactional
    public ChallengeResponseDto createChallenge(CreateChallengeRequest request) {
        String createdBy = getAuthenticatedUserEmail();
        User user = getAuthenticatedUser();
        boolean isAdmin = isAdmin();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestStartTime = now.toLocalDate().plusDays(1).atStartOfDay();
        if (request.getStartDate().isBefore(earliestStartTime.toLocalDate())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED,
                    "챌린지는 등록 후 최소 하루 뒤 자정(00:00)부터 시작할 수 있습니다.");
        }

        if (user.getDeposit() < request.getTargetAmount()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "예치금이 부족하여 챌린지를 생성할 수 없습니다.");
        }

        // 챌린지 생성 시 예치금 차감
        user.setDeposit(user.getDeposit() - request.getTargetAmount());
        userRepository.save(user);

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

        // 생성과 동시에 자동 참여 처리 (상태 "진행중")
        UserChallengeEntity participation = UserChallengeEntity.builder()
                .challenge(saved)
                .challengeName(saved.getChallengeName())
                .userId(user.getUserId())
                .depositAmount(request.getTargetAmount())
                .status("진행중")
                .createdAt(now)
                .updatedAt(now)
                .spendAmount(0)
                .build();
        userChallengeRepository.save(participation);

        // 서비스 거래내역 기록
        ServiceTransaction joinTx = ServiceTransaction.builder()
                .accountId(accountService.getServiceAccountId())
                .userId(user.getUserId())
                .transactionDate(now)
                .category("CHALLENGE_JOIN")
                .transactionType("WITHDRAW")
                .transactionBalance(request.getTargetAmount())
                .transactionAfterBalance(user.getDeposit())
                .description("챌린지 참여: " + saved.getChallengeName() + " 참여로 예치금 차감")
                .build();
        accountTransactionRepository.save(joinTx);
        logger.info("챌린지 생성 및 자동 참여 완료: 챌린지 ID = {}, 남은 예치금 = {}", saved.getChallengeId(), user.getDeposit());

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

        List<UserChallengeEntity> participations = userChallengeRepository
                .findByChallenge_ChallengeIdAndStatus(challengeId, "진행중");
        for (UserChallengeEntity participation : participations) {
            User user = userRepository.findById(participation.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));
            int refundAmount = participation.getDepositAmount();
            user.setDeposit(user.getDeposit() + refundAmount);
            userRepository.save(user);

            participation.setStatus("취소");
            participation.setUpdatedAt(LocalDateTime.now());
            userChallengeRepository.save(participation);

            ServiceTransaction refundTx = ServiceTransaction.builder()
                    .accountId(accountService.getServiceAccountId())
                    .userId(user.getUserId())
                    .transactionDate(LocalDateTime.now())
                    .category("CHALLENGE_DELETE_REFUND")
                    .transactionType("REFUND")
                    .transactionBalance(refundAmount)
                    .transactionAfterBalance(user.getDeposit())
                    .description("챌린지 삭제로 인한 예치금 환불")
                    .build();
            accountTransactionRepository.save(refundTx);
            logger.info("환불 거래내역 기록됨: {}", refundTx);
        }

        // 논리 삭제 처리
        challenge.setDeleted(true);
        challengeRepository.save(challenge);
        logger.info("챌린지 삭제 완료: ID = {}, Name = {}", challenge.getChallengeId(), challenge.getChallengeName());
    }

    // 챌린지 수정은 항상 불가능
    @Transactional
    public ChallengeResponseDto updateChallenge(Integer challengeId, CreateChallengeRequest request) {
        throw new CustomException(ErrorCode.UNAUTHORIZED, "챌린지는 수정할 수 없습니다.");
    }

    // 공식 챌린지 조회: soft delete되지 않고, activeFlag가 false이며, 종료일이 오늘 이후(또는 오늘 포함)인 챌린지
    @Cacheable(value = "officialChallenges", key = "#page + '-' + #size")
    public Page<ChallengeResponseDto> getOfficialChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository
                .findByChallengeTypeAndDeletedFalseAndActiveFlagFalseAndEndDateGreaterThanEqual(
                        "공식챌린지", LocalDate.now(), PageRequest.of(page, size));
        List<ChallengeResponseDto> dtos = challenges.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, challenges.getPageable(), challenges.getTotalElements());
    }

    // 유저 챌린지 조회: 위와 동일 조건 적용
    @Cacheable(value = "userChallenges", key = "#page + '-' + #size")
    public Page<ChallengeResponseDto> getUserChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository
                .findByChallengeTypeAndDeletedFalseAndActiveFlagFalseAndEndDateGreaterThanEqual(
                        "유저챌린지", LocalDate.now(), PageRequest.of(page, size));
        List<ChallengeResponseDto> dtos = challenges.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, challenges.getPageable(), challenges.getTotalElements());
    }

    // 인증 정보에서 로그인한 사용자 이메일 조회
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

    //
    // 로그인한 User 객체 조회
    public User getAuthenticatedUser() {
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
        // startDate가 오늘보다 이후면 아직 시작 안한 것으로 간주: !startDate.isAfter(today)
        List<ChallengeEntity> challenges = challengeRepository.findByStartDateBeforeAndActiveFlagFalse(today.plusDays(1));
        for (ChallengeEntity challenge : challenges) {
            if (!challenge.getDeleted()) { // soft delete된 건 제외
                // 오늘이 시작일과 같거나 지난 경우 활성화
                if (!challenge.getStartDate().isAfter(today)) {
                    challenge.setActiveFlag(true);
                    challengeRepository.save(challenge);
                    logger.debug("챌린지 활성화됨: ID = {}, Name = {}", challenge.getChallengeId(), challenge.getChallengeName());
                }
            }
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

    @Transactional
    public ChallengeDetailResponseDto getChallengeDetail(Integer challengeId) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        List<UserChallengeEntity> participants = userChallengeRepository.findByChallenge_ChallengeIdAndStatus(challengeId, "진행중");

        // ✅ 로그인한 유저
        User currentUser = getAuthenticatedUser();
        Integer myUserId = currentUser.getUserId();
        int mySpendingAmount;

        logger.debug("📍 challenge.getEndDate()={}, LocalDate.now()={}", challenge.getEndDate(), LocalDate.now());

        // ✅ 챌린지 기간이면 실시간 계산 + DB에 최신화
        if (challenge.getEndDate().isAfter(LocalDate.now())) {
            int freshSpending = calculateUserSpendingForChallengeCategory(challenge, myUserId);
            logger.info("💸 계산된 소비 금액 = {}", freshSpending);

            mySpendingAmount = freshSpending;

            UserChallengeEntity myParticipation = participants.stream()
                    .filter(p -> p.getUserId() == myUserId)
                    .findFirst()
                    .orElse(null);
            if (myParticipation != null) {
                myParticipation.setSpendAmount(freshSpending);
                myParticipation.setUpdatedAt(LocalDateTime.now());
                userChallengeRepository.save(myParticipation);
                userChallengeRepository.flush();
                logger.info("✅ spendAmount 업데이트됨: {}", freshSpending);
            }
        } else {
            // 이미 종료된 경우 DB 값 그대로 사용
            mySpendingAmount = participants.stream()
                    .filter(p -> p.getUserId() == myUserId)
                    .map(UserChallengeEntity::getSpendAmount)
                    .findFirst()
                    .orElse(0);
        }

        // ✅ 아래는 그대로 통계 계산 로직
        double totalRateSum = 0.0;
        int count = 0;
        int bucketOver100 = 0;
        int bucket100to85 = 0;
        int bucket84to50 = 0;
        int bucket49to25 = 0;
        int bucket24to0 = 0;

        for (UserChallengeEntity participation : participants) {
            int userSpending = participation.getSpendAmount();
            int limit = challenge.getLimitAmount();
            double rate = limit > 0 ? ((double) userSpending / limit) * 100.0 : 0.0;

            totalRateSum += rate;
            count++;

            if (rate > 100.0) bucketOver100++;
            else if (rate >= 85.0) bucket100to85++;
            else if (rate >= 50.0) bucket84to50++;
            else if (rate >= 25.0) bucket49to25++;
            else bucket24to0++;
        }

        double averageRate = count > 0 ? totalRateSum / count : 0.0;

        return ChallengeDetailResponseDto.builder()
                .challenge(mapToDto(challenge))
                .participantCount(participants.size())
                .bucketOver100(bucketOver100)
                .bucket100to85(bucket100to85)
                .bucket84to50(bucket84to50)
                .bucket49to25(bucket49to25)
                .bucket24to0(bucket24to0)
                .averageSuccessRate(averageRate)
                .mySpendingAmount(mySpendingAmount) // ✅ 추가된 필드
                .build();
    }




    @Transactional(readOnly = true)
    public List<PastChallengeResponseDto> getPastParticipatedChallenges() {
        User user = getAuthenticatedUser();
        List<UserChallengeEntity> participations = userChallengeRepository.findByUserIdAndStatusNot(user.getUserId(), "진행중");
        LocalDate today = LocalDate.now();
        return participations.stream()
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
                            .participationStatus(participation.getStatus())
                            .thumbnailUrl(mapToDto(challenge).getThumbnailUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 챌린지 종료 후, 성공한 참여자에게 환불 처리 (성공한 참여자에게 총 예치금 풀을 나눠줌)
    @Transactional
    public void settleChallenge(Integer challengeId) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        if (!challenge.getEndDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지가 아직 종료되지 않았습니다.");
        }
        List<UserChallengeEntity> allParticipants = userChallengeRepository.findByChallenge_ChallengeId(challengeId);
        if (allParticipants.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "참여 기록이 없습니다.");
        }
        List<UserChallengeEntity> successfulParticipants = allParticipants.stream()
                .filter(p -> "성공".equals(p.getStatus()))
                .collect(Collectors.toList());
        if (successfulParticipants.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "성공한 참여자가 없어 환불할 수 없습니다.");
        }
        int totalPool = allParticipants.stream()
                .mapToInt(UserChallengeEntity::getDepositAmount)
                .sum();
        int refundPerParticipant = totalPool / successfulParticipants.size();

        for (UserChallengeEntity participation : successfulParticipants) {
            User user = userRepository.findById(participation.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "참여자의 사용자 정보를 찾을 수 없습니다."));
            user.setDeposit(user.getDeposit() + refundPerParticipant);
            userRepository.save(user);
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

    // 사용자 소비 내역을 기반으로 챌린지 카테고리 점수를 계산 (예: { "주유"=50000, "쇼핑"=30000, ... })
    public Map<String, Integer> calculateChallengeCategoryScores(User user) {
        List<Transaction> transactions = transactionRepository.findByUserId(user.getUserId());
        Map<String, Integer> categoryScores = new HashMap<>();
        for (Transaction tx : transactions) {
            if (tx.getTransactionType() == 2 && tx.getCategoryId() != null) {
                Optional<CategoryEntity> optCategory = categoryRepository.findById(tx.getCategoryId());
                if (optCategory.isPresent() && optCategory.get().getChallengeCategoryId() != null) {
                    String challengeCategory = String.valueOf(optCategory.get().getChallengeCategoryId());
                    categoryScores.merge(challengeCategory, tx.getAmount(), Integer::sum);
                }
            }
        }
        return categoryScores;
    }

    /**
     * 추천 챌린지 조회
     * 사용자의 소비 내역을 기반으로 챌린지 카테고리 점수를 계산한 후, 챌린지의 challengeCategory와 매칭하여
     * 높은 점수 순으로 최대 12개 챌린지를 추천합니다.
     */
    @Transactional
    public List<ChallengeResponseDto> recommendChallengesForUser() {
        String email = getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        Map<String, Integer> challengeCategoryScores = calculateChallengeCategoryScores(user);
        logger.info("사용자 소비 기반 챌린지 카테고리 점수: {}", challengeCategoryScores);

        // 추천 대상은 soft delete되지 않았고, 아직 시작되지 않았으며(active_flag false), 종료일이 오늘 이후(또는 오늘 포함)인 챌린지
        List<ChallengeEntity> allChallenges = challengeRepository.findAll();
        List<ChallengeEntity> sortedChallenges = allChallenges.stream()
                .filter(ch -> !ch.getDeleted()
                        && !ch.getActiveFlag()
                        && !ch.getEndDate().isBefore(LocalDate.now()))
                .sorted((c1, c2) -> {
                    int score1 = challengeCategoryScores.getOrDefault(c1.getChallengeCategory(), 0);
                    int score2 = challengeCategoryScores.getOrDefault(c2.getChallengeCategory(), 0);
                    return Integer.compare(score2, score1);
                })
                .limit(12)
                .collect(Collectors.toList());

        return sortedChallenges.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    /**
     * 해당 챌린지에 참여한 유저의, 챌린지 기간 동안 해당 챌린지 카테고리에 대해 소비한 금액을 계산합니다.
     */
    @Transactional(readOnly = true)
    public int calculateUserSpendingForChallengeCategory(ChallengeEntity challenge, Integer userId) {
        LocalDateTime challengeStart = challenge.getStartDate().atStartOfDay();
        LocalDateTime challengeEnd = challenge.getEndDate().atTime(23, 59, 59);

        logger.debug("📅 챌린지 기간: {} ~ {}", challengeStart, challengeEnd);
        logger.debug("🎯 챌린지 카테고리 (문자열): {}", challenge.getChallengeCategory());

        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        logger.debug("💾 사용자 거래내역 총 개수: {}", transactions.size());

        int totalSpending = 0;

        for (Transaction tx : transactions) {
            LocalDateTime txDate = tx.getTransactionDate();
            if (txDate == null) {
                logger.warn("❗ 거래 날짜가 null: txId={}, amount={}", tx.getTransactionId(), tx.getAmount());
                continue;
            }

            if (txDate.isBefore(challengeStart) || txDate.isAfter(challengeEnd)) {
                logger.debug("⛔ 날짜 범위 밖 거래: txId={}, txDate={}", tx.getTransactionId(), txDate);
                continue;
            }

            if (tx.getTransactionType() != 2) {
                logger.debug("❌ 출금 아님: txId={}, txType={}", tx.getTransactionId(), tx.getTransactionType());
                continue;
            }

            if (tx.getCategoryId() == null) {
                logger.debug("❌ 카테고리 없음: txId={}", tx.getTransactionId());
                continue;
            }

            Optional<CategoryEntity> optCategory = categoryRepository.findById(tx.getCategoryId());
            if (optCategory.isEmpty()) {
                logger.warn("❓ 존재하지 않는 카테고리 ID: txId={}, categoryId={}", tx.getTransactionId(), tx.getCategoryId());
                continue;
            }

            CategoryEntity category = optCategory.get();
            if (category.getChallengeCategoryId() == null) {
                logger.debug("🚨 조건 테스트: txId={}, type={}, catId={}, catChallengeId={}, challengeCat={}, amount={}",
                        tx.getTransactionId(),
                        tx.getTransactionType(),
                        tx.getCategoryId(),
                        category.getChallengeCategoryId(),
                        challenge.getChallengeCategory(),
                        tx.getAmount());
                logger.debug("❌ challengeCategoryId가 null: categoryId={}", category.getCategoryId());
                continue;
            }

            String txChallengeCategoryName = category.getCategoryName(); // "택시"
            String challengeCategory = challenge.getChallengeCategory(); // "택시"

            if (!txChallengeCategoryName.equals(challengeCategory)) {
                logger.debug("❌ 챌린지 카테고리 불일치 (이름 기준): txCategoryName={}, challengeCategory={}, txId={}",
                        txChallengeCategoryName, challengeCategory, tx.getTransactionId());
                continue;
            }


            // ✅ 최종 소비 인정
            logger.info("✅ 소비 인정: txId={}, amount={}, 카테고리={}", tx.getTransactionId(), tx.getAmount(), txChallengeCategoryName);
            totalSpending += tx.getAmount();
        }

        logger.info("💰 최종 소비 금액 = {}", totalSpending);
        return totalSpending;
    }




    /**
     * 챌린지 종료 시점에, 참여 유저별로 해당 챌린지 카테고리 소비 금액을 산출하여,
     * 챌린지의 limit_amount 이하이면 "성공", 초과하면 "실패"로 참여 상태를 업데이트합니다.
     * (챌린지 종료일이 지난 경우에만 실행)
     */
    @Transactional
    public void evaluateChallengeOutcome(Integer challengeId) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        if (!challenge.getEndDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지가 아직 종료되지 않았습니다.");
        }
        List<UserChallengeEntity> participants = userChallengeRepository
                .findByChallenge_ChallengeIdAndStatus(challengeId, "진행중");
        for (UserChallengeEntity participation : participants) {
            int spending = calculateUserSpendingForChallengeCategory(challenge, participation.getUserId());
            participation.setSpendAmount(spending);
            if (spending <= challenge.getLimitAmount()) {
                participation.setStatus("성공");
            } else {
                participation.setStatus("실패");
            }
            participation.setUpdatedAt(LocalDateTime.now());
            userChallengeRepository.save(participation);
        }
    }

    /**
     * 자정에 실행되어 종료된 챌린지에 대해 평가 및 환불 정산을 진행합니다.
     * soft delete된 챌린지는 제외하고, active_flag가 true인 챌린지 중에서 처리합니다.
     * 처리 후 active_flag를 false로 업데이트하여 조회에서 제외되도록 합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void processEndedChallenges() {
        // soft delete되지 않았고, active_flag가 true이며, 종료일이 오늘 이전인 챌린지 조회
        List<ChallengeEntity> endedChallenges = challengeRepository
                .findByEndDateBeforeAndDeletedFalseAndActiveFlagTrue(LocalDate.now());
        for (ChallengeEntity challenge : endedChallenges) {
            try {
                evaluateChallengeOutcome(challenge.getChallengeId());
                settleChallenge(challenge.getChallengeId());
                // 종료 후 active_flag를 false로 전환
                challenge.setActiveFlag(false);
                challengeRepository.save(challenge);
                logger.info("챌린지 {} 평가 및 환불 정산 완료", challenge.getChallengeId());
            } catch (Exception e) {
                logger.error("챌린지 {} 처리 중 오류 발생: {}", challenge.getChallengeId(), e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PastChallengeResponseDto> getUnnotifiedResults() {
        User user = getAuthenticatedUser();
        List<UserChallengeEntity> results = userChallengeRepository
                .findByUserIdAndStatusInAndNotified(user.getUserId(), List.of("성공", "실패"), false);

        return results.stream()
                .map(participation -> {
                    ChallengeEntity challenge = participation.getChallenge();
                    return PastChallengeResponseDto.builder()
                            .challengeId(challenge.getChallengeId())
                            .challengeName(challenge.getChallengeName())
                            .challengeType(challenge.getChallengeType())
                            .startDate(challenge.getStartDate())
                            .endDate(challenge.getEndDate())
                            .description(challenge.getDescription())
                            .participationStatus(participation.getStatus())
                            .thumbnailUrl(mapToDto(challenge).getThumbnailUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsNotified(Integer userId) {
        List<UserChallengeEntity> results = userChallengeRepository
                .findByUserIdAndStatusInAndNotified(userId, List.of("성공", "실패"), false);
        for (UserChallengeEntity result : results) {
            result.setNotified(true);
            result.setUpdatedAt(LocalDateTime.now());
            userChallengeRepository.save(result);
        }
    }

}
