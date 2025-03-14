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

    // JOIN을 통한 사용자의 결제 내역 조회
    @Query("SELECT p FROM PaymentHistoryEntity p JOIN CardEntity c ON p.cardId = c.cardId " +
            "WHERE c.userId = :userId AND p.transactionDate BETWEEN :startDate AND :endDate")
    List<PaymentHistoryEntity> findByUserIdAndTransactionDateBetween(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // JOIN을 통한 사용자의 특정 날짜 결제 내역 조회
    @Query("SELECT p FROM PaymentHistoryEntity p JOIN CardEntity c ON p.cardId = c.cardId " +
            "WHERE c.userId = :userId AND p.transactionDate = :date")
    List<PaymentHistoryEntity> findByUserIdAndTransactionDate(
            @Param("userId") Integer userId,
            @Param("date") LocalDate date);

    // 낭비 소비 표시 업데이트
    @Query("UPDATE PaymentHistoryEntity p SET p.isWaste = :isWaste " +
            "WHERE p.paymentHistoryId = :paymentHistoryId")
    void updateIsWaste(
            @Param("paymentHistoryId") Integer paymentHistoryId,
            @Param("isWaste") Boolean isWaste);
}