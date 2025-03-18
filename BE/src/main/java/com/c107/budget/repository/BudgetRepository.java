package com.c107.budget.repository;

import com.c107.budget.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Integer> {
    Optional<BudgetEntity> findByEmailAndCategoryIdAndStartDateAndEndDate(
            String email,
            Integer categoryId,
            LocalDate startDate,
            LocalDate endDate
    );
    List<BudgetEntity> findByEmailAndStartDateAndEndDate(
            String email,
            LocalDate startDate,
            LocalDate endDate
    );
}