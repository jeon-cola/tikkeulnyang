package com.c107.challenge.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PastChallengeResponseDto {
    private Integer challengeId;
    private String challengeName;
    private String challengeType;
    private Integer targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String createdBy;
    private Integer maxParticipants;
    private Boolean activeFlag;
    private String challengeCategory;
    private LocalDateTime createdAt;
    private Integer limitAmount;
    private String participationStatus;
    private String thumbnailUrl;
}
