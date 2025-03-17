package com.c107.budget.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequestDto {
    private Integer categoryId;
    private Integer amount;
}
