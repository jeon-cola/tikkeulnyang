package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.CheckCardBenefit;
import com.c107.recommendcard.entity.CheckCardBenefitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckCardBenefitRepository extends JpaRepository<CheckCardBenefit, CheckCardBenefitId> {
    @Query("SELECT cb FROM CheckCardBenefit cb WHERE cb.recoCardId = :sourceCardId")
    List<CheckCardBenefit> findBySourceCardId(@Param("sourceCardId") Integer sourceCardId);

    List<CheckCardBenefit> findByRecoCardId(Integer sourceCardId);
}