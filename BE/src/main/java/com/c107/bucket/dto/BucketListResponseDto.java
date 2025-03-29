package com.c107.bucket.dto;


import com.c107.bucket.entity.BucketEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketListResponseDto {
    private Integer bucket_id;
    private String title;
    private Integer current_savings;  // saved_amount
    private Integer target_amount;    // amount
    private String category;
    private String savings_account;
    private String transfer_account;  // withdrawal_account
    private String saving_days;
    private String description;
    private Integer count;
    private String status;
    private LocalDateTime created_at;

    public static BucketListResponseDto fromEntity(BucketEntity entity, String categoryName) {
        return BucketListResponseDto.builder()
                .bucket_id(entity.getBucketId())
                .title(entity.getTitle())
                .current_savings(entity.getSavedAmount())
                .target_amount(entity.getAmount())
                .category(categoryName)
                .savings_account(entity.getSavingAccount())
                .transfer_account(entity.getWithdrawalAccount())
                .saving_days(entity.getSavingDays())
                .description(entity.getDescription())
                .count(entity.getCount())
                .status(entity.getStatus())
                .created_at(entity.getCreatedAt())
                .build();
    }
}