package com.c107.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceTransactionDto {
    private LocalDateTime transactionDate;
    private String category;      // DEPOSIT_CHARGE, DEPOSIT_REFUND, CHALLENGE_JOIN, CHALLENGE_DELETE_REFUND, CHALLENGE_SETTLE_REFUND
    private String description;
}
