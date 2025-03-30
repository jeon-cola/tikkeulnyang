package com.c107.share.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ShareCommentDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentRequest {
        private String comment;
        private Integer emoji; // 0: 좋아요, 1: 멋져요, 2: 화이팅
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentResponse {
        private Long commentId;
        private Integer userId;
        private String userNickname;
        private String profileImageUrl;
        private String comment;
        private Integer emoji;
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyCommentsResponse {
        private String date;
        private List<CommentResponse> comments;

        @JsonProperty("emoji_counts")
        private EmojiCounts emojiCounts;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmojiCounts {
        private Integer like; // 0 이모지 개수
        private Integer cool; // 1 이모지 개수
        private Integer fighting; // 2 이모지 개수
    }
}