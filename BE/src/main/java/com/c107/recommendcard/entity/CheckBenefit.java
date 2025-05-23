package com.c107.recommendcard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "check_benefits")
@Getter
@Setter
public class CheckBenefit {

    @Id
    @Column(name = "benefit_id")
    private Integer benefitId;

    @Column(name = "category")
    private String category;
}
