package com.c107.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRemainResponseDto {
    private Totals totals;
    private List<Budget> budgets;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Totals {
        @JsonProperty("total_remaining_amount")
        private Integer totalRemainingAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Budget {
        @JsonProperty("category_id")
        private Integer categoryId;

        @JsonProperty("remaining_amount")
        private Integer remainingAmount;
    }
}