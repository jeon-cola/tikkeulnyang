package com.c107.bucket.service;

import com.c107.accounts.entity.Account;
import com.c107.accounts.repository.AccountRepository;
import com.c107.bucket.dto.*;
import com.c107.bucket.entity.BucketCategoryEntity;
import com.c107.bucket.entity.BucketEntity;
import com.c107.bucket.enums.DayOfWeek;
import com.c107.bucket.repository.BucketCategoryRepository;
import com.c107.bucket.repository.BucketRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BucketService {

    private static final Logger logger = LoggerFactory.getLogger(BucketService.class);

    private final BucketRepository bucketRepository;
    private final BucketCategoryRepository bucketCategoryRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Value("${finance.api.key}")
    private String financeApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 카테고리 목록 조회
     * 캐시 이름: bucketCategories
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "bucketCategories")
    public List<BucketCategoryResponseDto> getAllCategories() {
        List<BucketCategoryEntity> categories = bucketCategoryRepository.findAll();
        return categories.stream()
                .map(BucketCategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 버킷리스트 생성 기능
     * 버킷리스트 조회 캐시(bucketLists)를 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = "bucketLists", key = "#email")
    public BucketResponseDto.Response createBucket(String email, BucketResponseDto.Request request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        BucketCategoryEntity category = bucketCategoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 카테고리입니다."));

        BucketEntity bucket = BucketEntity.builder()
                .userId(user.getUserId())
                .bucketCategoryId(request.getCategory())
                .title(request.getTitle())
                .amount(request.getAmount())
                .isCompleted(false)
                .build();

        BucketEntity savedBucket = bucketRepository.save(bucket);
        logger.info("버킷리스트 저장 완료: {}", savedBucket.getBucketId());

        return BucketResponseDto.Response.fromEntity(savedBucket, category.getCategoryName());
    }

    /**
     * 저축 날짜와 금액 설정
     * 버킷리스트 조회 캐시(bucketLists)를 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = "bucketLists", key = "#email")
    public BucketDateDto.Response setBucketSavingConfig(String email, BucketDateDto.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        validateDaysOfWeek(request.getSaving_days());

        // 가장 최근 생성된 버킷리스트 가져오기
        BucketEntity bucket = bucketRepository.findTopByUserIdOrderByCreatedAtDesc(user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

        // 저축 날짜와 금액 업데이트
        bucket.setSavingAmount(request.getSaving_amount());
        bucket.setSavingDays(request.getSaving_days());
        bucket.setStatus("시작중");
        bucket.setIsCompleted(true); // is_completed 필드를 true로 설정

        BucketEntity updatedBucket = bucketRepository.save(bucket);
        logger.info("버킷리스트 저축 설정 완료: {}", updatedBucket.getBucketId());

        return BucketDateDto.Response.builder()
                .saving_amount(updatedBucket.getSavingAmount())
                .save_days(updatedBucket.getSavingDays())
                .created_at(LocalDateTime.now())
                .status(updatedBucket.getStatus())
                .build();
    }

    private void validateDaysOfWeek(String savingDays) {
        if (savingDays == null || savingDays.isEmpty()) {
            return; // 빈 값 허용 시
        }

        List<String> validDays = DayOfWeek.getAllKoreanNames();
        List<String> selectedDays = Arrays.asList(savingDays.split(","));

//      // 유효한 요일 체크 - 필요 시 주석 해제
//      for (String day : selectedDays) {
//          if (!validDays.contains(day.trim())) {
//              throw new CustomException(ErrorCode.INVALID_REQUEST, "유효하지 않은 요일이 포함되어 있습니다: " + day);
//          }
//      }
    }

    /**
     * 버킷리스트에 계좌 설정
     * 버킷리스트 조회 캐시(bucketLists)를 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = "bucketLists", key = "#email")
    public BucketAccountDto.Response setBucketAccounts(String email, BucketAccountDto.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 가장 최근 생성된 버킷리스트 가져오기
        BucketEntity bucket = bucketRepository.findTopByUserIdOrderByCreatedAtDesc(user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

        // 계좌 번호 추출
        String savingAccountNo = request.getSaving_account().getAccount_number();
        String withdrawalAccountNo = request.getWithdrawal_account().getAccount_number();

        // 저축통장 정보 가져오기
        Account savingAccount = accountRepository.findByAccountNumber(savingAccountNo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "저축통장 정보를 찾을 수 없습니다."));

        // 출금통장 정보 가져오기
        Account withdrawalAccount = accountRepository.findByAccountNumber(withdrawalAccountNo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "출금통장 정보를 찾을 수 없습니다."));

        if (!savingAccount.getUserId().equals(user.getUserId()) ||
                !withdrawalAccount.getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "해당 계좌에 대한 권한이 없습니다.");
        }

        // 은행명 추가 유효성 검사 (선택적)
        if (!savingAccount.getBankName().equals(request.getSaving_account().getBank_name())) {
            logger.warn("요청된 저축계좌 은행명과 실제 은행명이 일치하지 않습니다");
        }

        if (!withdrawalAccount.getBankName().equals(request.getWithdrawal_account().getBank_name())) {
            logger.warn("요청된 출금계좌 은행명과 실제 은행명이 일치하지 않습니다");
        }

        bucket.setSavingAccount(savingAccountNo);
        bucket.setWithdrawalAccount(withdrawalAccountNo);

        BucketEntity updatedBucket = bucketRepository.save(bucket);
        LocalDateTime updatedAt = LocalDateTime.now();

        BucketAccountDto.AccountInfo savingAccountInfo = BucketAccountDto.AccountInfo.builder()
                .bank_name(savingAccount.getBankName())
                .account_number(savingAccount.getAccountNumber())
                .build();

        BucketAccountDto.AccountInfo withdrawalAccountInfo = BucketAccountDto.AccountInfo.builder()
                .bank_name(withdrawalAccount.getBankName())
                .account_number(withdrawalAccount.getAccountNumber())
                .build();

        return BucketAccountDto.Response.builder()
                .bucket_id(updatedBucket.getBucketId())
                .updated_at(updatedAt)
                .saving_account(savingAccountInfo)
                .withdrawal_account(withdrawalAccountInfo)
                .build();
    }

    /**
     * 버킷리스트 조회 기능
     * 캐시 이름: bucketLists, 키: email
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "bucketLists", key = "#email")
    public List<BucketListResponseDto> getBucketLists(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<BucketEntity> buckets = bucketRepository.findByUserId(user.getUserId());

        return buckets.stream()
                .map(bucket -> {
                    String categoryName = bucketCategoryRepository.findById(bucket.getBucketCategoryId())
                            .map(BucketCategoryEntity::getCategoryName)
                            .orElse("미분류");

                    return BucketListResponseDto.fromEntity(bucket, categoryName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 버킷리스트 삭제
     * 버킷리스트 관련 캐시(bucketLists)를 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = "bucketLists", key = "#email")
    public void deleteBucket(String email, Integer bucketId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        BucketEntity bucket = bucketRepository.findByBucketIdAndUserId(bucketId, user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

        bucketRepository.delete(bucket);
        logger.info("버킷리스트 삭제 완료: {}", bucketId);
    }

    /**
     * 계좌이체 및 저축 진행
     * 버킷리스트 관련 캐시(bucketLists)를 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = "bucketLists", key = "#email")
    public BucketSavingDto.Response processBucketSaving(String email, BucketSavingDto.Request request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        BucketEntity bucket = bucketRepository.findByBucketIdAndUserId(request.getBucketId(), user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

        Integer savingAmount = bucket.getSavingAmount();
        if (savingAmount == null || savingAmount <= 0) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "저축 금액이 설정되어 있지 않습니다.");
        }

        String withdrawalAccountNo = bucket.getWithdrawalAccount();
        String savingAccountNo = bucket.getSavingAccount();

        if (withdrawalAccountNo == null || savingAccountNo == null) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "출금계좌 또는 저축계좌가 설정되어 있지 않습니다.");
        }

        // 계좌 이체 API 호출
        transferMoney(user.getFinanceUserKey(), withdrawalAccountNo, savingAccountNo,
                String.valueOf(savingAmount));

        Integer currentSavedAmount = bucket.getSavedAmount() != null ? bucket.getSavedAmount() : 0;
        Integer newSavedAmount = currentSavedAmount + savingAmount;
        bucket.setSavedAmount(newSavedAmount);

        Integer currentCount = bucket.getCount() != null ? bucket.getCount() : 0;
        bucket.setCount(currentCount + 1);

        LocalDate expectedCompletionDate = calculateExpectedCompletionDate(bucket);
        String description = "예상 완료일: " + expectedCompletionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        bucket.setDescription(description);

        boolean isCompleted = newSavedAmount >= bucket.getAmount();
        if (isCompleted) {
            bucket.setStatus("완료");
        }

        BucketEntity updatedBucket = bucketRepository.save(bucket);

        return BucketSavingDto.Response.builder()
                .bucketId(updatedBucket.getBucketId())
                .withdrawalAccount(withdrawalAccountNo)
                .savingAccount(savingAccountNo)
                .totalSavedAmount(updatedBucket.getSavedAmount())
                .targetAmount(updatedBucket.getAmount())
                .count(updatedBucket.getCount())
                .status(updatedBucket.getStatus())
                .expectedCompletionDate(expectedCompletionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .isCompleted(isCompleted)
                .build();
    }

    // 계좌 이체 API 호출
    private void transferMoney(String userKey, String withdrawalAccountNo, String savingAccountNo, String amount) {
        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/updateDemandDepositAccountTransfer";
        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime
                + String.format("%06d", new Random().nextInt(1000000));

        Map<String, Object> header = new HashMap<>();
        header.put("apiName", "updateDemandDepositAccountTransfer");
        header.put("transmissionDate", transmissionDate);
        header.put("transmissionTime", transmissionTime);
        header.put("institutionCode", "00100");
        header.put("fintechAppNo", "001");
        header.put("apiServiceCode", "updateDemandDepositAccountTransfer");
        header.put("institutionTransactionUniqueNo", institutionTransactionUniqueNo);
        header.put("apiKey", financeApiKey);
        header.put("userKey", userKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);
        requestBody.put("depositAccountNo", savingAccountNo);
        requestBody.put("depositTransactionSummary", "(수시입출금) : 입금(이체)");
        requestBody.put("transactionBalance", amount);
        requestBody.put("withdrawalAccountNo", withdrawalAccountNo);
        requestBody.put("withdrawalTransactionSummary", "(수시입출금) : 출금(이체)");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        logger.info("계좌 이체 요청 시작: {}", LocalDateTime.now());
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, Object> responseMap = responseEntity.getBody();

        if (responseMap == null) {
            logger.error("계좌 이체 응답이 비어 있습니다.");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌 이체 응답이 비어 있습니다.");
        }
        logger.info("계좌 이체 완료: {}", responseMap);
    }

    // 예상 완료일 계산 메서드
    private LocalDate calculateExpectedCompletionDate(BucketEntity bucket) {
        Integer savedAmount = bucket.getSavedAmount() != null ? bucket.getSavedAmount() : 0;
        Integer count = bucket.getCount() != null ? bucket.getCount() : 0;
        Integer targetAmount = bucket.getAmount();

        // 저축 기록이 없는 경우
        if (count == 0) {
            // 기본값으로 3개월 후를 반환
            return LocalDate.now().plusMonths(3);
        }

        // 평균 저축 금액 계산
        double avgSavingAmount = (double) savedAmount / count;

        // 목표까지 남은 금액
        double remainingAmount = targetAmount - savedAmount;

        // 목표 달성까지 필요한 저축 횟수
        double requiredSavings = remainingAmount / avgSavingAmount;

        // 평균 저축 주기 계산 (첫 저축일로부터 지금까지 평균)
        LocalDateTime firstSavingDate = bucket.getCreatedAt(); // 첫 저축일을 생성일로 가정
        long daysSinceFirstSaving = ChronoUnit.DAYS.between(firstSavingDate.toLocalDate(), LocalDate.now());
        double avgSavingInterval = count > 1 ? (double) daysSinceFirstSaving / (count - 1) : 7; // 최소 일주일 간격으로 가정

        // 예상 완료일 계산
        long daysToCompletion = (long) Math.ceil(requiredSavings * avgSavingInterval);

        return LocalDate.now().plusDays(daysToCompletion);
    }
}
