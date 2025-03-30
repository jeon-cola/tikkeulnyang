package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.BudgetCheckMapping;
import com.c107.recommendcard.entity.BudgetCheckMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetCheckMappingRepository extends JpaRepository<BudgetCheckMapping, BudgetCheckMappingId> {
    List<BudgetCheckMapping> findByBudgetCategoryId(Integer budgetCategoryId);
}
