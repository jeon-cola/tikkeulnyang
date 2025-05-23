package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.CreditCardBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreditCardBenefitRepository extends JpaRepository<CreditCardBenefit, Long> {
    List<CreditCardBenefit> findByRecoCardId(Long recoCardId);

    List<CreditCardBenefit> findBySourceCardId(Integer sourceCardId);
}