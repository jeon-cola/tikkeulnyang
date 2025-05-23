package com.c107.challenge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_challenge_id")
    private Integer userChallengeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge; // 챌린지와 연관

    @Column(name = "challenge_name", nullable = false)
    private String challengeName;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "deposit_amount", nullable = false)
    private Integer depositAmount;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "spend_amount")
    private Integer spendAmount;

    @Column(name = "notified")
    private boolean notified;
}
