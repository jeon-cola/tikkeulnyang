package com.c107.recommendcard.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BudgetCheckMappingId implements Serializable {
    private Integer budgetCategoryId;
    private Integer benefitId;


}