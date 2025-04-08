package com.c107.transactions.repository;

import com.c107.transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
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
}