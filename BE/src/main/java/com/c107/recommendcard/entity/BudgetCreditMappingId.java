package com.c107.recommendcard.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BudgetCreditMappingId implements Serializable {
    private Integer budgetCategoryId;
    private Integer creditBenefitsId;

}
