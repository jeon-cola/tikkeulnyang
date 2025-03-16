package com.c107.paymenthistory.repository;

import com.c107.paymenthistory.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Integer> {
    Optional<CardEntity> findByCardNo(String cardNo);
    List<CardEntity> findByUserId(Integer userId);
}