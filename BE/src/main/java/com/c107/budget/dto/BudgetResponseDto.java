package com.c107.budget.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDto {
    private Budget budget;

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
}
