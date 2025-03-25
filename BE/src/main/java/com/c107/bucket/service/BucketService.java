package com.c107.bucket.service;

import com.c107.bucket.dto.BucketCategoryResponseDto;
import com.c107.bucket.dto.BucketListResponseDto;
import com.c107.bucket.dto.BucketResponseDto;
import com.c107.bucket.entity.BucketCategoryEntity;
import com.c107.bucket.entity.BucketEntity;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BucketService {

    private static final Logger logger = LoggerFactory.getLogger(BucketService.class);

    private final BucketRepository bucketRepository;
    private final BucketCategoryRepository bucketCategoryRepository;
    private final UserRepository userRepository;

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
