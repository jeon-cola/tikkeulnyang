package com.c107.budget.service;


import com.c107.budget.dto.BudgetRequestDto;
import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.common.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public BudgetResponseDto createBudget(String token, BudgetRequestDto requestDto) {
        // JWT에서 userId 추출
//        Integer userId = jwtUtil.
        Integer userId = 1;
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());


        budgetRepository.findByUserIdAndCategoryIdAndStartDateAndEndDate(
                        userId, requestDto.getCategoryId(), startDate, endDate)
                .ifPresent(budgetRepository::delete);


        BudgetEntity budgetEntity = BudgetEntity.builder()
                .userId(userId)
                .categoryId(requestDto.getCategoryId())
                .amount(requestDto.getAmount())
                .spendingAmount(0)
                .remainingAmount(requestDto.getAmount())
                .isExceed(false)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        BudgetEntity savedBudget = budgetRepository.save(budgetEntity);

        return BudgetResponseDto.builder()
                .budget(BudgetResponseDto.Budget.builder()
                        .categoryId(savedBudget.getCategoryId())
                        .amount(savedBudget.getAmount())
                        .spendingAmount(savedBudget.getSpendingAmount())
                        .remainingAmount(savedBudget.getRemainingAmount())
                        .isExceed(savedBudget.getIsExceed() ? 1 : 0)
                        .startDate(savedBudget.getStartDate().toString())
                        .endDate(savedBudget.getEndDate().toString())
                        .createdAt(savedBudget.getCreatedAt().toString())
                        .updatedAt(savedBudget.getUpdatedAt().toString())
                        .build())
                .build();
    }
}