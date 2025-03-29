package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.RecommendCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendCardRepository extends JpaRepository<RecommendCard, Integer> {
    List<RecommendCard> findByCardType(String cardType);
}
