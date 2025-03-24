package com.c107.cards.repository;


import com.c107.cards.entity.CardInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfoEntity, Integer> {
    List<CardInfoEntity> findByUserId(Integer userId);
    Optional<CardInfoEntity> findByCardNo(String cardNo);
    Optional<CardInfoEntity> findByUserIdAndCardNo(Integer userId, String cardNo);
}