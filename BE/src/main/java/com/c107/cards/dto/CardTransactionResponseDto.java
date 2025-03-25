package com.c107.cards.dto;

import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardTransactionResponseDto {

    private Integer year;
    private Integer month;

    @JsonProperty("total_spent")
    private Integer totalSpent;

    @JsonProperty("transactions")
    private List<Transaction> transactions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Transaction {
        private String date;

        @JsonProperty("category_id")
        private String categoryId;

        @JsonProperty("merchant_name")
        private String merchantName;

        @JsonProperty("category_name")
        private String categoryName;

        @JsonProperty("transaction_balance")
        private Integer transactionBalance;

        @JsonProperty("is_waste")
        private Integer isWaste;

        public static Transaction fromEntity(PaymentHistoryEntity entity) {
            return Transaction.builder()
                    .date(entity.getTransactionDate().toString())
                    .categoryId(entity.getCategoryId())
                    .merchantName(entity.getMerchantName())
                    .categoryName(entity.getCategoryName())
                    .transactionBalance(Integer.parseInt(entity.getTransactionBalance().trim().replace(",", "")))
                    .isWaste(entity.getIsWaste())
                    .build();
        }
    }
}