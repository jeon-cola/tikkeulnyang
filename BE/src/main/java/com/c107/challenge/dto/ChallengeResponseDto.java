package com.c107.challenge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ChallengeResponseDto {
    private Integer challengeId;
    private String challengeName;
    private String challengeType;
    private String targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String createdBy;
    private Boolean activeFlag;
    private String challengeCategory;
    private LocalDateTime createdAt;
    private Integer maxParticipants;
    private Integer limitAmount;
}
