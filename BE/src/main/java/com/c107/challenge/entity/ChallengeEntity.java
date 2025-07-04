package com.c107.challenge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Integer challengeId;

    @Column(name = "challenge_name", nullable = false, length = 20)
    private String challengeName;

    @Column(name = "challenge_type", nullable = false, length = 10)
    private String challengeType;

    @Column(name = "target_amount", nullable = false)
    private Integer targetAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "public_flag", nullable = false)
    private Boolean publicFlag;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "current_participants")
    private Integer currentParticipants;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "challenge_category", nullable = false, length = 10)
    private String challengeCategory;

    @Column(name = "limit_amount")
    private Integer limitAmount;

}
