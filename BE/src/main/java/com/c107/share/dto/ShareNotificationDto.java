package com.c107.share.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ShareNotificationDto {

    //  알림이 있는 날짜 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationDatesResponse {
        private List<String> dates;
    }

    // 단일 알림 정보 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationResponse {
        private Integer notificationId;
        private Integer shareId;
        private Integer interactionId;
        private String targetDate;

        @JsonProperty("is_read")
        private Integer isRead;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 알림 생성자 정보
        private Integer userId;
        private String userName;
        private String profileImageUrl;

        // 상호작용 내용 정보
        private String comment;
        private Integer emoji;
    }

    // 알림 읽음 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarkAsReadRequest {
        private Integer notificationId;
    }
}
