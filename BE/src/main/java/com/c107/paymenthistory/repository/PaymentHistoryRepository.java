package com.c107.paymenthistory.repository;

import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity, Integer> {
    // 카드 ID 목록과 날짜로 결제 내역 조회
    List<PaymentHistoryEntity> findByCardIdInAndTransactionDateBetween(
            List<Integer> cardIds,
            LocalDate startDate,
            LocalDate endDate
    );

    // 카드 ID 목록과 특정 날짜로 결제 내역 조회
    List<PaymentHistoryEntity> findByCardIdInAndTransactionDate(
            List<Integer> cardIds,
            LocalDate date
    );

    // 결재 내역 조회
    Optional<PaymentHistoryEntity> findById(Integer paymentHistoryId);

    List<PaymentHistoryEntity> findByCardIdInAndTransactionDateBetweenAndIsWaste(
            List<Integer> cardIds,
            LocalDate startDate,
            LocalDate endDate,
            Integer isWaste
    );

}