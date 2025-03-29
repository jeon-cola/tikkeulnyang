package com.c107.accounts.repository;

import com.c107.accounts.entity.ServiceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTransactionRepository extends JpaRepository<ServiceTransaction, Integer> {
    Optional<ServiceTransaction> findTopByAccountIdOrderByTransactionDateDesc(Integer accountId);

}
