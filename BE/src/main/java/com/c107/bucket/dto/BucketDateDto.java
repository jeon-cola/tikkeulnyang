package com.c107.bucket.dto;

import lombok.*;
import java.time.LocalDateTime;

public class BucketDateDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Integer saving_amount;
        private String saving_days;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer saving_amount;
        private String save_days;
        private LocalDateTime created_at;
        private String status;
    }
}