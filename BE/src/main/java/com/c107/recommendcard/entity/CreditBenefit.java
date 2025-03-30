package com.c107.recommendcard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "credit_benefits")
@Getter
@Setter
public class CreditBenefit {

    @Id
    @Column(name = "credit_benefits_id")
    private Integer creditBenefitsId;

    @Column(name = "category")
    private String category;
}
