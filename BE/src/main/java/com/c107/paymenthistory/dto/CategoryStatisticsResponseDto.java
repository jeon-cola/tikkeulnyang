package com.c107.paymenthistory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStatisticsResponseDto {
    private Integer year;
    private Integer month;

    @JsonProperty("goal_amount")
    private Integer goalAmount;

    @JsonProperty("spent_amount")
    private Integer spentAmount;

    @JsonProperty("remaining_amount")
    private Integer remainingAmount;

    private List<CategoryData> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryData {
        private String name;
        private Double percentage;
        private Integer amount;
    }
}