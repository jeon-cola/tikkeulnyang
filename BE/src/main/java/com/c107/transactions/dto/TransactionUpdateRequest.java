package com.c107.transactions.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionUpdateRequest {
    private Integer amount;
    private LocalDateTime transactionDate;
    private Integer categoryId;
    private String merchantName;
}

