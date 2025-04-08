package com.c107.recommendcard.repository;

import com.c107.recommendcard.entity.BudgetCheckMapping;
import com.c107.recommendcard.entity.BudgetCheckMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BudgetCheckMappingRepository extends JpaRepository<BudgetCheckMapping, BudgetCheckMappingId> {
    List<BudgetCheckMapping> findByBudgetCategoryId(Integer budgetCategoryId);

    Optional<BudgetCheckMapping> findByBenefitId(Integer benefitId);
}
