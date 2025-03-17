package com.c107.budget.repository;

import com.c107.budget.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Integer> {
    Optional<BudgetEntity> findByUserIdAndCategoryIdAndStartDateAndEndDate(
            Integer userId,
            Integer categoryId,
            LocalDate startDate,
            LocalDate endDate
    );
}