package com.c107.paymenthistory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentHistoryResponseDto {

    private Integer year;
    private Integer month;
    private String date;
    private Integer totalIncome;
    private Integer totalSpent;
    private List<DayData> data;
    private String status;
    private String message;
    private List<Transaction> transactions;
    private Integer paymentHistoryId;
    private String categoryId;
    @JsonProperty("is_waste")
    private Integer isWaste;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DayData {
        private String date;
        private Integer income;
        private Integer expense;
        private List<TransactionDetail> transactions;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionDetail {
        private String merchantName;
        private Integer amount;
        private String transactionType;
        private String category;
        private String description;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Transaction {
        private String date;
        private String categoryId;
        private String categoryName;
        private String merchantName;
        private String merchantId;
        private Integer transactionBalance;
        private Integer paymentHistoryId;
        private String transactionUniqueNo;
        private String category;
        private Integer amount;
        private String description;
        @JsonProperty("is_waste")
        private Integer isWaste;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WasteToggleRequest {
        @JsonProperty("payment_history_id")
        private Integer paymentHistoryId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WasteToggleResponse {
        @JsonProperty("payment_history_id")
        private Integer paymentHistoryId;
    }
}