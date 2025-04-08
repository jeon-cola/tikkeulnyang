package com.c107.accounts.repository;

import com.c107.accounts.entity.ServiceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceTransactionRepository extends JpaRepository<ServiceTransaction, Integer> {
    List<ServiceTransaction> findByUserIdAndCategoryIn(Integer userId, List<String> categories);
}
