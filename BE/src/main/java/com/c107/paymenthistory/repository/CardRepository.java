package com.c107.paymenthistory.repository;

import com.c107.paymenthistory.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Integer> {
    // 카드 번호로 카드 조회
    Optional<CardEntity> findByCardNo(String cardNo);
    // 사용자 id로 카드 목록 조회
    List<CardEntity> findByUserId(Integer userId);
}