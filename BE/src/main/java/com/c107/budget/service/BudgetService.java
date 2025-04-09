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
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TransactionRepository transactionRepository;

    /**
     * 예산 생성(또는 업데이트) 시 관련 캐시를 모두 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = {"budgetPlan", "budgetRemain", "wasteMoney", "categories", "totalBudget", "currentConsumption"}, allEntries = true)
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
     * 이전 달의 예산을 현재 달로 복사
     * 복사 시 관련 캐시를 무효화합니다.
     */
    @Transactional
    @CacheEvict(value = {"budgetPlan", "budgetRemain", "wasteMoney", "categories", "totalBudget", "currentConsumption"}, allEntries = true)
    public void copyPreviousMonthBudget(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 달의 시작일과 종료일
        LocalDate currentStartDate = LocalDate.of(year, month, 1);
        LocalDate currentEndDate = currentStartDate.withDayOfMonth(currentStartDate.lengthOfMonth());

        // 현재 달의 예산 확인
        List<BudgetEntity> currentBudgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, currentStartDate, currentEndDate);

        // 현재 달에 예산이 이미 있으면 복사하지 않음
        if (!currentBudgets.isEmpty()) {
            return;
        }

        // 이전 달의 시작일과 종료일
        LocalDate previousMonth = currentStartDate.minusMonths(1);
        LocalDate previousStartDate = LocalDate.of(previousMonth.getYear(), previousMonth.getMonthValue(), 1);
        LocalDate previousEndDate = previousStartDate.withDayOfMonth(previousStartDate.lengthOfMonth());

        // 이전 달의 예산 조회
        List<BudgetEntity> previousBudgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, previousStartDate, previousEndDate);

        // 이전 달 예산이 없으면 종료
        if (previousBudgets.isEmpty()) {
            return;
        }

        // 이전 달 예산을 현재 달로 복사
        List<BudgetEntity> newBudgets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (BudgetEntity previousBudget : previousBudgets) {
            BudgetEntity newBudget = BudgetEntity.builder()
                    .userId(user.getUserId())
                    .email(email)
                    .categoryId(previousBudget.getCategoryId())
                    .amount(previousBudget.getAmount())
                    .spendingAmount(0)  // 새 달이므로 지출액은 0으로 초기화
                    .remainingAmount(previousBudget.getAmount())  // 남은 금액은 전체 예산으로 설정
                    .isExceed(false)    // 초과 여부는 false로 초기화
                    .startDate(currentStartDate)
                    .endDate(currentEndDate)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            newBudgets.add(newBudget);
        }

        // 새 예산을 데이터베이스에 저장
        budgetRepository.saveAll(newBudgets);
    }

    /**
     * 특정 연/월에 대한 예산 계획 조회(예산이 없으면 이전 달 예산 복사)
     * 결과는 캐싱됩니다.
     */
    @Cacheable(value = "budgetPlan", key = "#email + '-' + #year + '-' + #month")
    public BudgetResponseDto getBudgetPlan(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 달의 예산 존재 여부 확인 없이 이전 달 예산 복사
        copyPreviousMonthBudget(email, year, month);

        // 현재 달의 시작일과 종료일
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 복사 후 현재 달의 예산 조회
        List<BudgetEntity> budgetEntities = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        // Transactions 테이블에서 실제 지출 내역 조회
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getUserId(),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 카테고리별 지출 금액 계산
        Map<Integer, Integer> categorySpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            try {
                // 입금 제외
                if (transaction.getTransactionType() != 1) {
                    Integer categoryId = transaction.getCategoryId();
                    int amount = transaction.getAmount();

                    if (categoryId != null) {
                        categorySpending.put(
                                categoryId,
                                categorySpending.getOrDefault(categoryId, 0) + amount
                        );
                    }
                }
            } catch (Exception e) {
                // 오류 발생 시 로깅
            }
        }

        int totalAmount = 0;
        int totalSpendingAmount = 0;
        int totalRemainingAmount = 0;
        boolean totalIsExceed = false;

        List<BudgetResponseDto.Budget> budgetDtos = new ArrayList<>();

        for (BudgetEntity budget : budgetEntities) {
            int amount = budget.getAmount() != null ? budget.getAmount() : 0;
            int spendingAmount = categorySpending.getOrDefault(budget.getCategoryId(), 0);
            int remainingAmount = amount - spendingAmount;
            boolean isExceed = spendingAmount > amount && amount > 0;

            totalAmount += amount;
            totalSpendingAmount += spendingAmount;
            totalRemainingAmount += remainingAmount;

            if (isExceed) {
                totalIsExceed = true;
            }

            BudgetResponseDto.Budget budgetDto = BudgetResponseDto.Budget.builder()
                    .categoryId(budget.getCategoryId())
                    .amount(amount)
                    .spendingAmount(spendingAmount)
                    .remainingAmount(remainingAmount)
                    .isExceed(isExceed ? 1 : 0)
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

    /**
     * 현재 달의 예산 잔액 조회
     * 캐싱 키에 현재 연도와 월을 포함하여 캐시 업데이트 시점을 구분합니다.
     */
    @Cacheable(value = "budgetRemain", key = "#email + '-' + T(java.time.LocalDate).now().getYear() + '-' + T(java.time.LocalDate).now().getMonthValue()")
    public BudgetRemainResponseDto getBudgetRemain(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<BudgetEntity> budgetEntities = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        // Transactions 테이블에서 실제 지출 내역 조회
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getUserId(),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 카테고리별 지출 금액 계산
        Map<Integer, Integer> categorySpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            try {
                // 입금 제외
                if (transaction.getTransactionType() != 1) {
                    Integer categoryId = transaction.getCategoryId();
                    int amount = transaction.getAmount();

                    if (categoryId != null) {
                        categorySpending.put(
                                categoryId,
                                categorySpending.getOrDefault(categoryId, 0) + amount
                        );
                    }
                }
            } catch (Exception e) {
                // 오류 발생 시 로깅
            }
        }

        int totalRemainingAmount = 0;
        List<BudgetRemainResponseDto.Budget> budgetDtos = new ArrayList<>();

        for (BudgetEntity budget : budgetEntities) {
            int amount = budget.getAmount() != null ? budget.getAmount() : 0;
            int spendingAmount = categorySpending.getOrDefault(budget.getCategoryId(), 0);
            int remainingAmount = amount - spendingAmount;

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

    /**
     * 낭비 금액 계산
     * 캐싱 키: email, 연도, 월
     */
    @Cacheable(value = "wasteMoney", key = "#email + '-' + #year + '-' + #month")
    public BudgetResponseDto.BudgetWaste getWasteMoney(String email, Integer year, Integer month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // transactions 테이블에서 데이터 조회
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getUserId(),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        int totalWasteAmount = 0;

        // 낭비로 표시된 거래(isWaste=1)만 필터링하여 합계 계산
        for (Transaction transaction : transactions) {
            try {
                // 입금(수입)은 제외하고 지출만 고려
                if (transaction.getTransactionType() != 1) {
                    Integer isWaste = transaction.getIsWaste();
                    // isWaste가 1인 경우에만 낭비 금액으로 계산
                    if (isWaste != null && isWaste == 1) {
                        totalWasteAmount += transaction.getAmount();
                    }
                }
            } catch (Exception e) {
            }
        }

        return BudgetResponseDto.BudgetWaste.builder()
                .year(year)
                .month(month)
                .totalWasteAmount(totalWasteAmount)
                .build();
    }

    /**
     * 사용자 설정 카테고리별 예산 및 실제 소비 내역 조회
     * 캐싱 키: email, 연도, 월
     */
    @Cacheable(value = "categories", key = "#email + '-' + #year + '-' + #month")
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

        // 해당 기간의 실제 카테고리별 소비 내역 조회
        List<Integer> userCardIds = cardRepository.findByUserId(user.getUserId())
                .stream()
                .map(CardEntity::getCardId)
                .collect(Collectors.toList());

        // Transactions 테이블에서 소비 내역 조회 (PaymentHistory 대신)
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getUserId(),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 카테고리별 실제 소비 금액 계산
        Map<Integer, Integer> categorySpending = new HashMap<>();

        for (Transaction transaction : transactions) {
            try {
                if (transaction.getTransactionType() != 1) { // 입금 제외
                    Integer categoryId = transaction.getCategoryId();
                    int amount = transaction.getAmount();
                    if (categoryId != null) {
                        categorySpending.put(
                                categoryId,
                                categorySpending.getOrDefault(categoryId, 0) + amount
                        );
                    }
                }
            } catch (Exception e) {
                // 오류 발생 시 로깅
            }
        }

        // 최종 계산을 위한 합계 변수들
        final int[] totalAmountArr = {0};
        final int[] totalSpendingAmountArr = {0};
        final int[] totalRemainingAmountArr = {0};
        final boolean[] totalIsExceedArr = {false};

        List<CategoryResponseDto.Category> categoryDtos = allCategories.stream()
                .map(category -> {
                    Integer categoryId = category.getBudgetCategoryId();
                    BudgetEntity budget = budgetMap.get(categoryId);
                    boolean hasBudget = budget != null;

                    int amount = 0;
                    int spendingAmount = categorySpending.getOrDefault(categoryId, 0);
                    int remainingAmount = 0;
                    int isExceed = 0;
                    String createdAt = null;
                    String updatedAt = null;

                    if (hasBudget) {
                        amount = budget.getAmount() != null ? budget.getAmount() : 0;
                        remainingAmount = amount - spendingAmount;
                        isExceed = (spendingAmount > amount && amount > 0) ? 1 : 0;
                        createdAt = budget.getCreatedAt() != null ? budget.getCreatedAt().toString() : null;
                        updatedAt = budget.getUpdatedAt() != null ? budget.getUpdatedAt().toString() : null;

                        totalAmountArr[0] += amount;
                        totalSpendingAmountArr[0] += spendingAmount;
                        totalRemainingAmountArr[0] += remainingAmount;
                        if (isExceed == 1) {
                            totalIsExceedArr[0] = true;
                        }
                    } else {
                        remainingAmount = -spendingAmount;
                        totalSpendingAmountArr[0] += spendingAmount;
                        totalRemainingAmountArr[0] -= spendingAmount;
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

        categoryDtos.sort(Comparator.comparing(CategoryResponseDto.Category::getCategoryId));

        CategoryResponseDto.Totals totals = CategoryResponseDto.Totals.builder()
                .totalAmount(totalAmountArr[0])
                .totalSpendingAmount(totalSpendingAmountArr[0])
                .totalRemainingAmount(totalRemainingAmountArr[0])
                .totalIsExceed(totalIsExceedArr[0] ? 1 : 0)
                .build();

        return CategoryResponseDto.builder()
                .totals(totals)
                .categories(categoryDtos)
                .build();
    }

    /**
     * 현재 달의 총 예산 조회
     * 캐싱 키: email-연도-월
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "totalBudget", key = "#email + '-' + T(java.time.LocalDate).now().getYear() + '-' + T(java.time.LocalDate).now().getMonthValue()")
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

    /**
     * 현재 달의 총 소비 금액 조회
     * 캐싱 키: email-연도-월
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "currentConsumption", key = "#email + '-' + T(java.time.LocalDate).now().getYear() + '-' + T(java.time.LocalDate).now().getMonthValue()")
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
