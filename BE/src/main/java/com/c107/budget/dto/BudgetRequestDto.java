package com.c107.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequestDto {


    @JsonProperty("category_id")
    private Integer categoryId;

    private Integer amount;
}
