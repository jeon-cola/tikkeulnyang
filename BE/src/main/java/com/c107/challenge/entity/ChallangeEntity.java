package com.c107.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Integer challengeId;

    @Column(name = "challenge_name")
    private String challengeName;

    @Column(name = "challenge_type")
    private String challengeType; // 예: 일반, NO_SPEND 등

    @Column(name = "target_amount")
    private String targetAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "min_required")
    private Integer minRequired;

    @Column(name = "public_flag")
    private Boolean publicFlag;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "current_participants")
    private Integer currentParticipants;
}
