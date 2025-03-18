package com.c107.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChallengeRequest {
    private String challengeName;
    private String challengeType;
    private String targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}