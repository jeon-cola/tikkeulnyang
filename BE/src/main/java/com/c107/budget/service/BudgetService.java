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

        System.out.println("Category ID: " + requestDto.getCategoryId());

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
}