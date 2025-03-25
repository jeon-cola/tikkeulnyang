package com.c107.bucket.dto;

import com.c107.bucket.entity.BucketCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BucketCategoryResponseDto {
    private Integer bucketCategoryId;
    private String categoryName;

    public static BucketCategoryResponseDto fromEntity(BucketCategoryEntity entity) {
        return BucketCategoryResponseDto.builder()
                .bucketCategoryId(entity.getBucketCategoryId())
                .categoryName(entity.getCategoryName())
                .build();
    }
}