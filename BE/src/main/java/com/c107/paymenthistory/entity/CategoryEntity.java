package com.c107.paymenthistory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", length = 20)
    private String categoryName;

    @Column(name = "budget_category_id")
    private Integer budgetCategory;

    @Column(name = "challenge_category_id")
    private Integer challengeCategoryId;

    @Column(name = "merchant_name", length = 20)
    private String merchantName;

    @Column(name = "category_code", length = 20)
    private String categoryCode;
}