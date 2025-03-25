package com.c107.paymenthistory.service;

import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.paymenthistory.dto.CategoryStatisticsResponseDto;
import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.entity.BudgetCategoryEntity;
import com.c107.paymenthistory.entity.CardEntity;
import com.c107.paymenthistory.entity.CategoryEntity;
import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import com.c107.paymenthistory.repository.BudgetCategoryRepository;
import com.c107.paymenthistory.repository.CardRepository;
import com.c107.paymenthistory.repository.CategoryRepository;
import com.c107.paymenthistory.repository.PaymentHistoryRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;


    @Transactional(readOnly = true)
    public PaymentHistoryResponseDto getConsumptionCalendar(
            String email,
            int year,
            int month,
            String type
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        Integer userId = user.getUserId();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Integer> userCardIds = cardRepository.findByUserId(userId)
                .stream()
                .map(CardEntity::getCardId)
                .collect(Collectors.toList());

        List<PaymentHistoryEntity> paymentHistories = paymentHistoryRepository
                .findByCardIdInAndTransactionDateBetween(userCardIds, startDate, endDate);

        Map<LocalDate, List<PaymentHistoryEntity>> paymentsByDate = paymentHistories.stream()
                .collect(Collectors.groupingBy(PaymentHistoryEntity::getTransactionDate));

        List<PaymentHistoryResponseDto.DayData> dayDataList = new ArrayList<>();
        int totalIncome = 0;
        int totalExpense = 0;

        for (int day = 1; day <= endDate.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            List<PaymentHistoryEntity> dayPayments = paymentsByDate.getOrDefault(date, Collections.emptyList());

            int dayIncome = 0;
            int dayExpense = 0;
            List<PaymentHistoryResponseDto.TransactionDetail> transactions = new ArrayList<>();

            for (PaymentHistoryEntity payment : dayPayments) {
                try {
                    int amount = Math.abs(Integer.parseInt(payment.getTransactionBalance().trim().replace(",", "")));
                    dayExpense += amount;
                    totalExpense += amount;

                    transactions.add(PaymentHistoryResponseDto.TransactionDetail.builder()
                            .merchantName(payment.getMerchantName())
                            .amount(amount)
                            .transactionType("EXPENSE")
                            .category(payment.getCategoryName())
                            .build());
                } catch (NumberFormatException | NullPointerException e) {
                    log.error("금액 변환 오류: {}", payment.getTransactionBalance(), e);
                }
            }

            dayDataList.add(PaymentHistoryResponseDto.DayData.builder()
                    .date(date.format(DateTimeFormatter.ISO_DATE))
                    .income(dayIncome)
                    .expense(dayExpense)
                    .transactions(transactions)
                    .build());
        }

        return PaymentHistoryResponseDto.builder()
                .year(year)
                .month(month)
                .totalIncome(totalIncome)
                .totalSpent(totalExpense)
                .data(dayDataList)
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentHistoryResponseDto getMonthlyConsumption(
            String email,
            Integer year,
            Integer month
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        Integer userId = user.getUserId();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Integer> userCardIds = cardRepository.findByUserId(userId)
                .stream()
                .map(CardEntity::getCardId)
                .collect(Collectors.toList());

        List<PaymentHistoryEntity> paymentHistories = paymentHistoryRepository
                .findByCardIdInAndTransactionDateBetween(userCardIds, startDate, endDate);

        // 카드 거래는 모두 지출로 처리
        int totalSpent = calculateTotalAmount(paymentHistories, false);
        // 현재는 카드 거래에서 수입을 처리하지 않음 (필요시 변경)
        int totalIncome = 0;

        List<Map<String, Object>> transactionsMap = new ArrayList<>();

        // Map을 사용하여 categoryId를 Integer로 직접 처리
        for (PaymentHistoryEntity p : paymentHistories) {
            try {
                int transactionAmount = Math.abs(Integer.parseInt(p.getTransactionBalance().trim().replace(",", "")));
                Map<String, Object> transaction = new HashMap<>();

                // 기본 필드 설정
                transaction.put("paymentHistoryId", p.getPaymentHistoryId());
                transaction.put("date", p.getTransactionDate().toString());
                transaction.put("categoryName", p.getCategoryName());
                transaction.put("merchantId", p.getMerchantId());
                transaction.put("merchantName", p.getMerchantName());
                transaction.put("transactionBalance", transactionAmount);
                transaction.put("amount", transactionAmount);
                transaction.put("category", p.getCategoryName());
                transaction.put("description", p.getMerchantName());
                transaction.put("transactionUniqueNo", p.getTransactionUniqueNo());
                transaction.put("is_waste", p.getIsWaste());

                // categoryId 처리 - 숫자로 변환 시도
                if (p.getCategoryId() != null && !p.getCategoryId().isEmpty()) {
                    try {
                        transaction.put("categoryId", Integer.parseInt(p.getCategoryId()));
                    } catch (NumberFormatException e) {
                        // 숫자로 변환 불가능한 경우 원래 값 사용
                        transaction.put("categoryId", p.getCategoryId());
                    }
                } else {
                    transaction.put("categoryId", null);
                }

                transactionsMap.add(transaction);
            } catch (Exception e) {
                log.error("결제 내역 처리 중 오류: {}", p.getPaymentHistoryId(), e);
            }
        }

        return PaymentHistoryResponseDto.builder()
                .year(year)
                .month(month)
                .totalSpent(totalSpent)
                .totalIncome(totalIncome)
                .transactionsMap(transactionsMap) // Map 리스트 직접 사용
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDailyConsumption(
            String email,
            String date
    ) {
        // 기존 코드 유지...
        // 익명 사용자 체크
        if ("anonymousUser".equals(email)) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        Integer userId = user.getUserId();

        LocalDate targetDate = LocalDate.parse(date);

        List<Integer> userCardIds = cardRepository.findByUserId(userId)
                .stream()
                .map(CardEntity::getCardId)
                .collect(Collectors.toList());

        List<PaymentHistoryEntity> paymentHistories = paymentHistoryRepository
                .findByCardIdInAndTransactionDate(userCardIds, targetDate);

        int totalSpent = calculateTotalAmount(paymentHistories, false);
        int totalIncome = 0; // 카드 거래는 모두 지출로 처리

        List<Map<String, Object>> transactions = paymentHistories.stream()
                .map(p -> {
                    int transactionAmount = Math.abs(Integer.parseInt(p.getTransactionBalance().trim().replace(",", "")));
                    Map<String, Object> transaction = new HashMap<>();

                    // 여기만 수정: categoryId를 Integer로 변환 시도
                    try {
                        if (p.getCategoryId() != null && !p.getCategoryId().isEmpty()) {
                            // categoryId를 Integer로 변환
                            transaction.put("categoryId", Integer.parseInt(p.getCategoryId()));
                        } else {
                            // null이거나 빈 문자열이면 null로 설정
                            transaction.put("categoryId", null);
                        }
                    } catch (NumberFormatException e) {
                        // 숫자 형식이 아니면 원본 문자열 그대로 유지
                        transaction.put("categoryId", p.getCategoryId());
                    }

                    transaction.put("category", p.getCategoryName());
                    transaction.put("merchantName", p.getMerchantName());
                    transaction.put("description", p.getMerchantName());
                    transaction.put("transactionBalance", transactionAmount);
                    transaction.put("is_waste", p.getIsWaste());
                    return transaction;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("totalIncome", totalIncome);
        response.put("totalSpent", totalSpent);
        response.put("transactions", transactions);

        return response;
    }

    private int calculateTotalAmount(List<PaymentHistoryEntity> paymentHistories, boolean isIncome) {
        int total = 0;
        for (PaymentHistoryEntity payment : paymentHistories) {
            try {
                int amount = Math.abs(Integer.parseInt(payment.getTransactionBalance().trim().replace(",", "")));

                // 카드 거래는 기본적으로 지출로 처리 (수입이 아닌 경우)
                if (!isIncome) {
                    total += amount;
                }
                // 수입 계산 로직이 필요하다면 여기에 추가 (현재는 카드 거래를 모두 지출로 간주)

            } catch (Exception e) {
                // 로그 추가
                log.error("금액 변환 중 오류 발생: {}", payment.getTransactionBalance(), e);
            }
        }
        return total;
    }

    // 낭비 상태 토글
    @Transactional
    public PaymentHistoryResponseDto.WasteToggleResponse toggleWasteStatus(
            String email,
            PaymentHistoryResponseDto.WasteToggleRequest request
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        PaymentHistoryEntity paymentHistory = paymentHistoryRepository
                .findById(request.getPaymentHistoryId())
                .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다."));

        CardEntity card = cardRepository.findById(paymentHistory.getCardId())
                .orElseThrow(() -> new RuntimeException("카드 정보를 찾을 수 없습니다."));

        if (!card.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 결제 내역에 대한 권한이 없습니다.");
        }

        // isWaste가 0이면 1로, 1이면 0으로 변경
        Boolean currentWasteStatus = paymentHistory.isWaste();
        paymentHistory.setIsWaste(currentWasteStatus == null || !currentWasteStatus);

        PaymentHistoryEntity updatedPaymentHistory = paymentHistoryRepository.save(paymentHistory);

        return PaymentHistoryResponseDto.WasteToggleResponse.builder()
                .paymentHistoryId(updatedPaymentHistory.getPaymentHistoryId())
                .build();
    }

    @Transactional
    public Integer toggleWasteStatus(Integer paymentHistoryId) {
        PaymentHistoryEntity paymentHistory = paymentHistoryRepository
                .findById(paymentHistoryId)
                .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다."));

        Boolean currentWasteStatus = paymentHistory.isWaste();
        paymentHistory.setIsWaste(currentWasteStatus == null || !currentWasteStatus);

        PaymentHistoryEntity updatedPaymentHistory = paymentHistoryRepository.save(paymentHistory);

        return updatedPaymentHistory.getIsWaste();
    }


    // 카테고리별 통계
    @Transactional(readOnly = true)
    public CategoryStatisticsResponseDto getCategoryStatistics(String email, Integer year, Integer month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        List<Integer> userCardIds = cardRepository.findByUserId(user.getUserId())
                .stream()
                .map(CardEntity::getCardId)
                .collect(Collectors.toList());

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<BudgetEntity> budgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        int goalAmount = budgets.stream()
                .mapToInt(budget -> budget.getAmount() != null ? budget.getAmount() : 0)
                .sum();

        List<PaymentHistoryEntity> payments = paymentHistoryRepository
                .findByCardIdInAndTransactionDateBetween(userCardIds, startDate, endDate);

        Map<Integer, String> budgetCategoryMap = new HashMap<>();
        List<BudgetCategoryEntity> allBudgetCategories = budgetCategoryRepository.findAll();
        for (BudgetCategoryEntity bc : allBudgetCategories) {
            budgetCategoryMap.put(bc.getBudgetCategoryId(), bc.getCategoryName());
        }


        Map<String, Integer> categoryAmounts = new HashMap<>();
        int totalSpent = 0;

        for (PaymentHistoryEntity payment : payments) {
            try {
                String categoryIdStr = payment.getCategoryId();
                Integer categoryId = null;

                try {
                    categoryId = Integer.parseInt(categoryIdStr);
                } catch (NumberFormatException e) {
                    log.warn("카테고리 ID를 파싱할 수 없습니다: {}", categoryIdStr);
                    continue;
                }

                CategoryEntity category = categoryId != null ?
                        categoryRepository.findById(categoryId).orElse(null) : null;

                int amount = Math.abs(Integer.parseInt(payment.getTransactionBalance().trim().replace(",", "")));
                totalSpent += amount;

                // 예산 카테고리 매핑
                String budgetCategoryName = "결제";
                if (category != null && category.getBudgetCategory() != null) {
                    // 이미 Integer 타입이므로 parseInt 사용 X
                    Integer budgetCategoryId = category.getBudgetCategory();
                    budgetCategoryName = budgetCategoryMap.get(budgetCategoryId);

                    if (budgetCategoryName == null) {
                        log.warn("budget_category ID {}에 해당하는 이름을 찾을 수 없습니다. categoryId: {}",
                                budgetCategoryId, categoryId);
                        budgetCategoryName = "기타";
                    } else {
                        log.debug("카테고리 매핑 성공: ID={}, 예산카테고리ID={}, 예산카테고리명={}, 결제내역ID={}",
                                categoryId, budgetCategoryId, budgetCategoryName, payment.getPaymentHistoryId());
                    }
                } else {
                    log.warn("카테고리 ID {}에 해당하는 정보가 없거나 budget_category가 null입니다. 결제내역ID: {}",
                            categoryId, payment.getPaymentHistoryId());
                }

                // 카테고리별 금액 누적
                categoryAmounts.put(budgetCategoryName,
                        categoryAmounts.getOrDefault(budgetCategoryName, 0) + amount);

            } catch (Exception e) {
                log.error("결제 내역 처리 중 오류 발생: {}", payment.getPaymentHistoryId(), e);
            }
        }

        List<CategoryStatisticsResponseDto.CategoryData> categories = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryAmounts.entrySet()) {
            double percentage = totalSpent > 0 ? (entry.getValue() * 100.0) / totalSpent : 0;

            categories.add(CategoryStatisticsResponseDto.CategoryData.builder()
                    .name(entry.getKey())
                    .amount(entry.getValue())
                    .percentage(Math.round(percentage * 10) / 10.0) // 소수점 첫째 자리까지
                    .build());
        }

        categories.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));

        int remainingAmount = goalAmount - totalSpent;

        return CategoryStatisticsResponseDto.builder()
                .year(year)
                .month(month)
                .goalAmount(goalAmount)
                .spentAmount(totalSpent)
                .remainingAmount(remainingAmount)
                .categories(categories)
                .build();
    }
}