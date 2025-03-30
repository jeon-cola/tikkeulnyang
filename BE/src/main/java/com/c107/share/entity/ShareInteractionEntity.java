package com.c107.share.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "share_interaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareInteractionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Integer interactionId;

    @Column(name = "share_id", nullable = false)
    private Integer shareId;

    @Column(name = "user_id", nullable = false)
    private Integer userId; // 댓글을 작성한 사용자

    @Column(name = "target_date")
    private LocalDate targetDate; // 댓글이 달린 가계부 날짜

    @Column(name = "comment_content")
    private String commentContent; // 댓글 내용

    @Column(name = "emoji")
    private Integer emoji; // 이모지 (0: 좋아요, 1: 멋져요, 2: 화이팅)

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 시간
}