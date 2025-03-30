package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.CheckCardBenefit;
import com.c107.recommendcard.entity.CheckCardBenefitId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CheckCardBenefitRepository extends JpaRepository<CheckCardBenefit, CheckCardBenefitId> {
    List<CheckCardBenefit> findByRecoCardId(Long recoCardId);
}