package com.c107.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDto {
    private Budget budget;

    private Totals totals;
    private List<Budget> budgets;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Totals {
        @JsonProperty("total_amount")
        private Integer totalAmount;

        @JsonProperty("total_spending_amount")
        private Integer totalSpendingAmount;

        @JsonProperty("total_remaining_amount")
        private Integer totalRemainingAmount;

        @JsonProperty("total_is_exceed")
        private Integer totalIsExceed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Budget {
        private Integer categoryId;
        private Integer amount;
        private Integer spendingAmount;
        private Integer remainingAmount;
        private Integer isExceed;
        private String startDate;
        private String endDate;
        private String createdAt;
        private String updatedAt;


    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetWaste {
        private Integer year;
        private Integer month;

        @JsonProperty("total_waste_amount")
        private Integer totalWasteAmount;
    }
}
