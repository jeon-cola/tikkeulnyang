package com.c107.recommendcard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "credit_card_benefits")
@Getter
@Setter
@IdClass(CreditCardBenefitId.class)
public class CreditCardBenefit {

    @Id
    @Column(name = "reco_card_id")
    private Long recoCardId;

    @Column(name = "reco_card_id")
    private Integer sourceCardId;

    @Id
    @Column(name = "credit_benefits_id")
    private Integer creditBenefitsId;

    @Column(name = "description")
    private String description;
}
