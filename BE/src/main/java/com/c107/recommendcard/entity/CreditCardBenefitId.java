package com.c107.recommendcard.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class CreditCardBenefitId implements Serializable {
    private Long recoCardId;
    private Integer creditBenefitsId;

}
