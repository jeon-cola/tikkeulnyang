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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BucketService {

    private static final Logger logger = LoggerFactory.getLogger(BucketService.class);

    private final BucketRepository bucketRepository;
    private final BucketCategoryRepository bucketCategoryRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    // 카테고리 목록 조회 메서드
    @Transactional(readOnly = true)
    public List<BucketCategoryResponseDto> getAllCategories() {
        List<BucketCategoryEntity> categories = bucketCategoryRepository.findAll();
        return categories.stream()
                .map(BucketCategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 버킷리스트 생성 기능
    @Transactional
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

    // 저축 날짜와 금액 설정
    @Transactional
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

    //
    private void validateDaysOfWeek(String savingDays) {
        if (savingDays == null || savingDays.isEmpty()) {
            return; // 빈 값 허용 시
        }

        List<String> validDays = DayOfWeek.getAllKoreanNames();
        List<String> selectedDays = Arrays.asList(savingDays.split(","));

//        for (String day : selectedDays) {
//            if (!validDays.contains(day.trim())) {
//                throw new CustomException(ErrorCode.INVALID_REQUEST, "유효하지 않은 요일이 포함되어 있습니다: " + day);
//            }
//        }
    }


    @Transactional
    public BucketAccountDto.Response setBucketAccounts(String email, BucketAccountDto.Request request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 가장 최근 생성된 버킷리스트 가져오기
        BucketEntity bucket = bucketRepository.findTopByUserIdOrderByCreatedAtDesc(user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

        // 저축통장 정보 가져오기
        Account savingAccount = accountRepository.findByAccountNumber(request.getSaving_account())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "저축통장 정보를 찾을 수 없습니다."));

        // 출금통장 정보 가져오기
        Account withdrawalAccount = accountRepository.findByAccountNumber(request.getWithdrawal_account())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "출금통장 정보를 찾을 수 없습니다."));

        // 사용자가 계좌의 소유자인지 확인
        if (!savingAccount.getUserId().equals(user.getUserId()) ||
                !withdrawalAccount.getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "해당 계좌에 대한 권한이 없습니다.");
        }

        // 계좌 정보 업데이트
        bucket.setSavingAccount(request.getSaving_account());
        bucket.setWithdrawalAccount(request.getWithdrawal_account());

        BucketEntity updatedBucket = bucketRepository.save(bucket);
        LocalDateTime updatedAt = LocalDateTime.now();

        // 응답 생성
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

    // 버킷리스트 조회 기능
    @Transactional(readOnly = true)
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

    // 버킷리스트 삭제
    @Transactional
    public void deleteBucket(String email, Integer bucketId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        BucketEntity bucket = bucketRepository.findByBucketIdAndUserId(bucketId, user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

        bucketRepository.delete(bucket);
        logger.info("버킷리스트 삭제 완료: {}", bucketId);
    }
}
