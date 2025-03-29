package com.c107.paymenthistory.repository;

import com.c107.paymenthistory.entity.BudgetCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategoryEntity, Integer> {
    Optional<BudgetCategoryEntity> findByCategoryName(String categoryName);

}