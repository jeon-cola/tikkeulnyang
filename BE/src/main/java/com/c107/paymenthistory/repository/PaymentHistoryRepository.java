package com.c107.paymenthistory.repository;

import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity, Integer> {
    // 사용자 ID와 날짜로 결제 내역 조회 (카드와 조인)
    @Query("SELECT p FROM PaymentHistoryEntity p JOIN CardEntity c ON p.cardId = c.cardId " +
            "WHERE c.userId = :userId AND p.transactionDate BETWEEN :startDate AND :endDate")
    List<PaymentHistoryEntity> findByUserIdAndTransactionDateBetween(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 카드 ID 목록과 날짜로 결제 내역 조회
    List<PaymentHistoryEntity> findByCardIdInAndTransactionDateBetween(
            List<Integer> cardIds,
            LocalDate startDate,
            LocalDate endDate
    );
    // 사용자 ID와 특정 날짜로 결제 내역 조회
    @Query("SELECT p FROM PaymentHistoryEntity p JOIN CardEntity c ON p.cardId = c.cardId " +
            "WHERE c.userId = :userId AND p.transactionDate = :date")
    List<PaymentHistoryEntity> findByUserIdAndTransactionDate(
            @Param("userId") Integer userId,
            @Param("date") LocalDate date
    );

}