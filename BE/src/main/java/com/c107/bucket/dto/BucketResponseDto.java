package com.c107.bucket.dto;

import com.c107.bucket.entity.BucketEntity;
import lombok.*;

import java.time.LocalDateTime;

public class BucketResponseDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Integer category; // bucket_category_id 값을 받음
        private String title;
        private Integer amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String bucketId;
        private Integer category; // bucket_category_id 값을 그대로 반환
        private String categoryName; // 카테고리 이름도 함께 반환 (선택사항)
        private String title;
        private Integer amount;
        private LocalDateTime createdAt;

        public static Response fromEntity(BucketEntity entity, String categoryName) {
            return Response.builder()
                    .bucketId(entity.getBucketId().toString())
                    .category(entity.getBucketCategoryId())
                    .categoryName(categoryName)
                    .title(entity.getTitle())
                    .amount(entity.getAmount())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}