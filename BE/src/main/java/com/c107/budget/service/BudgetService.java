package com.c107.budget.service;


import com.c107.budget.dto.BudgetRemainResponseDto;
import com.c107.budget.dto.BudgetRequestDto;
import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.dto.CategoryResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.common.util.JwtUtil;
import com.c107.paymenthistory.entity.BudgetCategoryEntity;
import com.c107.paymenthistory.entity.CardEntity;
import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import com.c107.paymenthistory.repository.BudgetCategoryRepository;
import com.c107.paymenthistory.repository.CardRepository;
import com.c107.paymenthistory.repository.PaymentHistoryRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final CardRepository cardRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;

    @Transactional
    public BudgetResponseDto createBudget(String email, BudgetRequestDto requestDto, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 기존 예산 조회 (같은 월, 같은 카테고리)
        Optional<BudgetEntity> existingBudget = budgetRepository.findByEmailAndCategoryIdAndStartDateAndEndDate(
                email, requestDto.getCategoryId(), startDate, endDate);

        // 기존 예산이 있으면 업데이트, 없으면 새로 생성
        BudgetEntity budgetEntity;
        if (existingBudget.isPresent()) {
            budgetEntity = existingBudget.get();
            budgetEntity.setAmount(requestDto.getAmount());
            budgetEntity.setRemainingAmount(requestDto.getAmount() - budgetEntity.getSpendingAmount());
            budgetEntity.setUpdatedAt(LocalDateTime.now());
        } else {
            // 새 예산 생성
            budgetEntity = BudgetEntity.builder()
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
        }

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

    /**
     * 특정 연/월에 대한 예산 계획 조회
     */
    public BudgetResponseDto getBudgetPlan(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

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

    public BudgetRemainResponseDto getBudgetRemain(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<BudgetEntity> budgetEntities = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        int totalRemainingAmount = 0;

        List<BudgetRemainResponseDto.Budget> budgetDtos = new ArrayList<>();

        for (BudgetEntity budget : budgetEntities) {
            int remainingAmount = budget.getRemainingAmount() != null ? budget.getRemainingAmount() : 0;
            totalRemainingAmount += remainingAmount;

            BudgetRemainResponseDto.Budget budgetDto = BudgetRemainResponseDto.Budget.builder()
                    .categoryId(budget.getCategoryId())
                    .remainingAmount(remainingAmount)
                    .build();

            budgetDtos.add(budgetDto);
        }

        BudgetRemainResponseDto.Totals totals = BudgetRemainResponseDto.Totals.builder()
                .totalRemainingAmount(totalRemainingAmount)
                .build();

        return BudgetRemainResponseDto.builder()
                .totals(totals)
                .budgets(budgetDtos)
                .build();
    }

    // 낭비 금액 계산
    public BudgetResponseDto.BudgetWaste getWasteMoney(String email, Integer year, Integer month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Integer> userCardIds = cardRepository.findByUserId(user.getUserId())
                .stream()
                .map(CardEntity::getCardId)
                .collect(Collectors.toList());

        List<PaymentHistoryEntity> wastePayments = paymentHistoryRepository
                .findByCardIdInAndTransactionDateBetweenAndIsWaste(
                        userCardIds, startDate, endDate, 1);

        int totalWasteAmount = 0;
        for (PaymentHistoryEntity payment : wastePayments) {
            int amount = Math.abs(Integer.parseInt(payment.getTransactionBalance().trim().replace(",", "")));
            totalWasteAmount += amount;
        }

        return BudgetResponseDto.BudgetWaste.builder()
                .year(year)
                .month(month)
                .totalWasteAmount(totalWasteAmount)
                .build();
    }

    public CategoryResponseDto getAllCategories(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 사용자가 설정한 예산 조회
        List<BudgetEntity> userBudgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        // 카테고리 ID와 예산 엔티티 매핑
        Map<Integer, BudgetEntity> budgetMap = userBudgets.stream()
                .collect(Collectors.toMap(
                        BudgetEntity::getCategoryId,
                        budget -> budget,
                        (existing, replacement) -> existing
                ));

        // 모든 카테고리 조회
        List<BudgetCategoryEntity> allCategories = budgetCategoryRepository.findAll();

        // 총합 계산 (기존 getBudgetPlan과 동일하게)
        int totalAmount = 0;
        int totalSpendingAmount = 0;
        int totalRemainingAmount = 0;
        boolean totalIsExceed = false;

        for (BudgetEntity budget : userBudgets) {
            totalAmount += budget.getAmount() != null ? budget.getAmount() : 0;
            totalSpendingAmount += budget.getSpendingAmount() != null ? budget.getSpendingAmount() : 0;
            totalRemainingAmount += budget.getRemainingAmount() != null ? budget.getRemainingAmount() : 0;

            if (budget.getIsExceed() != null && budget.getIsExceed()) {
                totalIsExceed = true;
            }
        }

        // DTO 변환
        List<CategoryResponseDto.Category> categoryDtos = allCategories.stream()
                .map(category -> {
                    Integer categoryId = category.getBudgetCategoryId();
                    BudgetEntity budget = budgetMap.get(categoryId);
                    boolean hasBudget = budget != null;

                    // 기본값 설정
                    Integer amount = 0;
                    Integer spendingAmount = 0;
                    Integer remainingAmount = 0;
                    Integer isExceed = 0;
                    String createdAt = null;
                    String updatedAt = null;

                    // 예산이 설정된 경우 실제 값으로 업데이트
                    if (hasBudget) {
                        amount = budget.getAmount();
                        spendingAmount = budget.getSpendingAmount();
                        remainingAmount = budget.getRemainingAmount();
                        isExceed = budget.getIsExceed() != null && budget.getIsExceed() ? 1 : 0;
                        createdAt = budget.getCreatedAt() != null ? budget.getCreatedAt().toString() : null;
                        updatedAt = budget.getUpdatedAt() != null ? budget.getUpdatedAt().toString() : null;
                    }

                    return CategoryResponseDto.Category.builder()
                            .categoryId(categoryId)
                            .categoryName(category.getCategoryName())
                            .hasBudget(hasBudget)
                            .amount(amount)
                            .spendingAmount(spendingAmount)
                            .remainingAmount(remainingAmount)
                            .isExceed(isExceed)
                            .startDate(startDate.toString())
                            .endDate(endDate.toString())
                            .createdAt(createdAt)
                            .updatedAt(updatedAt)
                            .build();
                })
                .collect(Collectors.toList());

        // 카테고리별 정렬 (카테고리 ID 기준)
        categoryDtos.sort(Comparator.comparing(CategoryResponseDto.Category::getCategoryId));

        // 총합 정보 설정
        CategoryResponseDto.Totals totals = CategoryResponseDto.Totals.builder()
                .totalAmount(totalAmount)
                .totalSpendingAmount(totalSpendingAmount)
                .totalRemainingAmount(totalRemainingAmount)
                .totalIsExceed(totalIsExceed ? 1 : 0)
                .build();

        return CategoryResponseDto.builder()
                .totals(totals)
                .categories(categoryDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public Integer getTotalBudget(String email) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<BudgetEntity> budgetEntities = budgetRepository.findByEmailAndStartDateAndEndDate(email, startDate, endDate);

        int totalBudget = budgetEntities.stream()
                .mapToInt(budget -> budget.getAmount() != null ? budget.getAmount() : 0)
                .sum();
        return totalBudget;
    }

    @Transactional(readOnly = true)
    public Integer getCurrentConsumption(String email) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<BudgetEntity> budgetEntities = budgetRepository.findByEmailAndStartDateAndEndDate(email, startDate, endDate);

        int totalConsumption = budgetEntities.stream()
                .mapToInt(budget -> budget.getSpendingAmount() != null ? budget.getSpendingAmount() : 0)
                .sum();
        return totalConsumption;
    }

}