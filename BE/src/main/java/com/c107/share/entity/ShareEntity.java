package com.c107.share.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "share")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private Integer shareId;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "shared_user_id")
    private Integer sharedUserId;

    @Column(name = "invitation_link")
    private String invitationLink;

    @Column(name = "link_expire")
    private LocalDateTime linkExpire;

    // 공유 상태 (0: 공유 전, 1: 공유 중, 2: 공유 해제)
    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}

