package com.c107.recommendcard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "check_card_benefits")
@Getter
@Setter
@IdClass(CheckCardBenefitId.class)
public class CheckCardBenefit {

    @Id
    @Column(name = "reco_card_id")
    private Long recoCardId;

    @Id
    @Column(name = "benefit_id")
    private Integer benefitId;

    @Column(name = "description")
    private String description;
}
