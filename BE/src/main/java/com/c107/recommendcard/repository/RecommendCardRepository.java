package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.RecommendCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendCardRepository extends JpaRepository<RecommendCard, Integer> {
    @Query("SELECT DISTINCT rc FROM RecommendCard rc, CheckCardBenefit cb, CheckBenefit b " +
            "WHERE cb.recoCardId = rc.recoCardId AND cb.benefitId = b.benefitId AND b.category = :category")
    List<RecommendCard> findByBenefitCategory(@Param("category") String category);

    // 신용카드 혜택 기준 추천 카드 조회
    @Query("SELECT DISTINCT rc FROM RecommendCard rc, CreditCardBenefit ccb, CreditBenefit cb " +
            "WHERE ccb.recoCardId = rc.recoCardId " +
            "AND ccb.creditBenefitsId = cb.creditBenefitsId " +
            "AND cb.category = :category " +
            "AND rc.cardType = '신용카드'")
    List<RecommendCard> findByCreditBenefitCategory(@Param("category") String category);

    // 새롭게 추가: credit_benefits_id 목록으로 조회 (매핑 적용용)
    @Query("SELECT DISTINCT rc FROM RecommendCard rc, CreditCardBenefit ccb, CreditBenefit cb " +
            "WHERE ccb.recoCardId = rc.recoCardId " +
            "AND ccb.creditBenefitsId = cb.creditBenefitsId " +
            "AND cb.creditBenefitsId IN :creditBenefitIds " +
            "AND rc.cardType = '신용카드'")
    List<RecommendCard> findByCreditBenefitIds(@Param("creditBenefitIds") List<Integer> creditBenefitIds);
}
