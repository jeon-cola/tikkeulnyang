package com.c107.budget.service;


import com.c107.budget.dto.BudgetRequestDto;
import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.common.util.JwtUtil;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public BudgetResponseDto createBudget(String email, BudgetRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());


        budgetRepository.findByEmailAndCategoryIdAndStartDateAndEndDate(
                        email, requestDto.getCategoryId(), startDate, endDate)
                .ifPresent(budgetRepository::delete);


        BudgetEntity budgetEntity = BudgetEntity.builder()
                .userId(user.getUserId())
                .email(email)
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

    public BudgetResponseDto getBudgetPlan(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<BudgetEntity> budgetEntities = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        int totalAmount = 0;
        int totalSpendingAmount = 0;
        int totalRemainingAmount = 0;
        boolean totalIsExceed = false;

        List<BudgetResponseDto.Budget> budgetDtos = new ArrayList<>();

        for (BudgetEntity budget : budgetEntities) {
            totalAmount += budget.getAmount() != null ? budget.getAmount() : 0;
            totalSpendingAmount += budget.getSpendingAmount() != null ? budget.getSpendingAmount() : 0;
            totalRemainingAmount += budget.getRemainingAmount() != null ? budget.getRemainingAmount() : 0;

            if (budget.getIsExceed() != null && budget.getIsExceed()) {
                totalIsExceed = true;
            }

            BudgetResponseDto.Budget budgetDto = BudgetResponseDto.Budget.builder()
                    .categoryId(budget.getCategoryId())
                    .amount(budget.getAmount())
                    .spendingAmount(budget.getSpendingAmount())
                    .remainingAmount(budget.getRemainingAmount())
                    .isExceed(budget.getIsExceed() != null && budget.getIsExceed() ? 1 : 0)
                    .startDate(budget.getStartDate().toString())
                    .endDate(budget.getEndDate().toString())
                    .createdAt(budget.getCreatedAt().toString())
                    .updatedAt(budget.getUpdatedAt().toString())
                    .build();

            budgetDtos.add(budgetDto);
        }

        BudgetResponseDto.Totals totals = BudgetResponseDto.Totals.builder()
                .totalAmount(totalAmount)
                .totalSpendingAmount(totalSpendingAmount)
                .totalRemainingAmount(totalRemainingAmount)
                .totalIsExceed(totalIsExceed ? 1 : 0)
                .build();

        return BudgetResponseDto.builder()
                .totals(totals)
                .budgets(budgetDtos)
                .build();
    }
}