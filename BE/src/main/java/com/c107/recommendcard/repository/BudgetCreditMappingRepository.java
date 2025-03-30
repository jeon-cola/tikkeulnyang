package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.BudgetCreditMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetCreditMappingRepository extends JpaRepository<BudgetCreditMapping, Long> {
    List<BudgetCreditMapping> findByBudgetCategoryId(Integer budgetCategoryId);
}
