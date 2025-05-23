package com.c107.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    // 기존 BudgetResponseDto와 유사한 총합 정보
    private Totals totals;
    private List<Category> categories;

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
    public static class Category {
        private Integer categoryId;
        private String categoryName;
        private boolean hasBudget;  // 사용자가 예산을 설정했는지 여부
        private Integer amount;     // 설정된 예산 금액 (없으면 0)
        private Integer spendingAmount; // 실제 지출 금액
        private Integer remainingAmount; // 남은 예산 금액
        private Integer isExceed;   // 예산 초과 여부
        private String startDate;   // 예산 시작일
        private String endDate;     // 예산 종료일
        private String createdAt;   // 생성일
        private String updatedAt;   // 수정일
    }
}