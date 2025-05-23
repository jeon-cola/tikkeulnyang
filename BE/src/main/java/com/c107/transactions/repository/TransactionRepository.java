package com.c107.transactions.repository;

import com.c107.transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findTopByAccountIdOrderByTransactionDateDesc(String accountNo);

    Optional<Transaction> findTopByAccountIdAndTransactionDateAndTransactionTypeAndAmount(
            String s, LocalDateTime txDateTime, int i, int txBalance);

    boolean existsByCardIdAndTransactionDateAndAmountAndMerchantName(
            Integer cardId,
            LocalDateTime transactionDate,
            Integer amount,
            String merchantName
    );

    // Modified methods to filter out deleted transactions
    List<Transaction> findByUserIdAndTransactionDateBetweenAndDeleted(
            Integer userId, LocalDateTime startDate, LocalDateTime endDate, Integer deleted);

    default List<Transaction> findByUserIdAndTransactionDateBetween(
            Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        return findByUserIdAndTransactionDateBetweenAndDeleted(
                userId, startDate, endDate, 0);
    }

    List<Transaction> findByUserIdAndDeleted(Integer userId, Integer deleted);

    default List<Transaction> findByUserId(Integer userId) {
        return findByUserIdAndDeleted(userId, 0);
    }

    List<Transaction> findAllByUserIdAndTransactionDateBetween(
            Integer userId, LocalDateTime start, LocalDateTime end);

    // TransactionRepository 인터페이스에 추가
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.userId = :userId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "AND t.transactionType != 1 " +  // 입금(수입) 제외
            "AND t.isWaste = 1 " +           // 낭비 표시된 것만
            "AND (t.deleted = 0 OR t.deleted IS NULL)")  // 삭제되지 않은 거래만
    Integer sumWasteAmountByUserIdAndPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}