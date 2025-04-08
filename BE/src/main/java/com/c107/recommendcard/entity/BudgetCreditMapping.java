package com.c107.recommendcard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "budget_credit_mapping")
@Getter
@Setter
@IdClass(BudgetCreditMappingId.class)
public class BudgetCreditMapping {

    @Id
    @Column(name = "credit_benefits_id")
    private Integer creditBenefitsId;

    @Id
    @Column(name = "budget_category_id")
    private Integer budgetCategoryId;
}
