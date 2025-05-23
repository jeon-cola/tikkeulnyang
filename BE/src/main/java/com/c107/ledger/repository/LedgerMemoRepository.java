package com.c107.ledger.repository;

import com.c107.ledger.entity.LedgerMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface LedgerMemoRepository extends JpaRepository<LedgerMemo, Integer> {
    Optional<LedgerMemo> findByMemoDate(LocalDate memoDate);
}
