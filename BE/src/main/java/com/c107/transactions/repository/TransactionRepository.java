package com.c107.transactions.repository;

import com.c107.transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findTopByAccountIdOrderByTransactionDateDesc(String accountNo);

    Optional<Transaction> findTopByAccountIdAndTransactionDateAndTransactionTypeAndAmount(String s, LocalDateTime txDateTime, int i, int txBalance);

    //Optional<Object> findByTransactionUniqueNo(String transactionUniqueNo);
}
