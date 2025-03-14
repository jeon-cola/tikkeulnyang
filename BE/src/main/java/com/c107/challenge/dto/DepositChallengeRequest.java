package com.c107.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositChallengeRequest {
    private Integer challengeId;
    private Integer userId;
    private String depositAmount;
}