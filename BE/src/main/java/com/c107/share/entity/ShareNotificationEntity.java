package com.c107.share.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "share_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "interaction_id")
    private Integer interactionId;

    @Column(name = "share_id")
    private Integer shareId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "is_read")
    private Integer isRead;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}