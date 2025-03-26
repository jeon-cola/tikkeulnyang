package com.c107.bucket.dto;

import lombok.*;
import java.time.LocalDateTime;

public class BucketAccountDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String saving_account;
        private String withdrawal_account;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountInfo {
        private String bank_name;
        private String account_number;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer bucket_id;
        private LocalDateTime updated_at;
        private AccountInfo saving_account;
        private AccountInfo withdrawal_account;
    }
}