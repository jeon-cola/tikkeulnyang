package com.c107.recommendcard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "budget_check_mapping")
@Getter
@Setter
@IdClass(BudgetCheckMappingId.class)
public class BudgetCheckMapping {

    @Id
    @Column(name = "benefit_id")
    private Integer benefitId;

    @Id
    @Column(name = "budget_category_id")
    private Integer budgetCategoryId;
}