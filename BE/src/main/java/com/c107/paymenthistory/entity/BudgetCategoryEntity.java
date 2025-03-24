package com.c107.paymenthistory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "budget_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCategoryEntity {

    @Id
    @Column(name = "budget_category_id")
    private Integer budgetCategoryId;

    @Column(name = "budget_category_name")
    private String categoryName;
}
