package com.c107.bucket.dto;

import lombok.*;
import java.time.LocalDate;

public class BucketSavingDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Integer bucketId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer bucketId;
        private String withdrawalAccount;
        private String savingAccount;
        private Integer amount;
        private Integer totalSavedAmount;
        private Integer targetAmount;
        private Integer count;
        private String status;
        private String expectedCompletionDate;
        private boolean isCompleted;
    }
}
