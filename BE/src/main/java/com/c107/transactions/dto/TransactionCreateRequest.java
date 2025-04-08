package com.c107.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateRequest {
    private Integer cardId;            // 카드 ID (없으면 기타로 처리됨)
    private Integer transactionType;   // 1: 수입, 2: 지출
    private Integer amount;            // 금액
    private Integer categoryId;        // budget_category_id 값
    private String merchantName;       // 가맹점명
    private Integer year;              // 연도
    private Integer month;             // 월
    private Integer day;               // 일
}