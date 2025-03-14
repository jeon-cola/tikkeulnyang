package com.c107.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt;
}