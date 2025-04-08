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
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final TransactionRepository transactionRepository;


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

        // payment_history 대신 transactions 테이블 사용
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        Map<LocalDate, List<Transaction>> transactionsByDate = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionDate().toLocalDate()));

        List<PaymentHistoryResponseDto.DayData> dayDataList = new ArrayList<>();
        int totalIncome = 0;
        int totalExpense = 0;

        for (int day = 1; day <= endDate.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            List<Transaction> dayTransactions = transactionsByDate.getOrDefault(date, Collections.emptyList());

            int dayIncome = 0;
            int dayExpense = 0;
            List<PaymentHistoryResponseDto.TransactionDetail> transactionDetails = new ArrayList<>();

            for (Transaction transaction : dayTransactions) {
                try {
                    int amount = transaction.getAmount();

                    // 거래 유형에 따라 수입 또는 지출로 분류
                    if (transaction.getTransactionType() == 1) { // 입금
                        dayIncome += amount;
                        totalIncome += amount;

                        transactionDetails.add(PaymentHistoryResponseDto.TransactionDetail.builder()
                                .merchantName(transaction.getMerchantName())
                                .amount(amount)
                                .transactionType("INCOME")
                                .category(getCategoryName(transaction.getCategoryId()))
                                .build());
                    } else { // 출금
                        dayExpense += amount;
                        totalExpense += amount;

                        transactionDetails.add(PaymentHistoryResponseDto.TransactionDetail.builder()
                                .merchantName(transaction.getMerchantName())
                                .amount(amount)
                                .transactionType("EXPENSE")
                                .category(getCategoryName(transaction.getCategoryId()))
                                .build());
                    }
                } catch (Exception e) {
                    log.error("금액 처리 오류: {}", transaction.getTransactionId(), e);
                }
            }

            dayDataList.add(PaymentHistoryResponseDto.DayData.builder()
                    .date(date.format(DateTimeFormatter.ISO_DATE))
                    .income(dayIncome)
                    .expense(dayExpense)
                    .transactions(transactionDetails)
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

    // 카테고리 ID로 카테고리 이름 조회 유틸 메서드
    private String getCategoryName(Integer categoryId) {
        if (categoryId == null) return "기타";

        return budgetCategoryRepository.findById(categoryId)
                .map(BudgetCategoryEntity::getCategoryName)
                .orElse("기타");
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

        // transactions 테이블에서 데이터 조회
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 디버깅을 위한 로그 추가
        log.info("조회된 거래 수: {}", transactions.size());
        for (Transaction t : transactions) {
            log.debug("거래 ID: {}, 타입: {}, 금액: {}, 카드ID: {}",
                    t.getTransactionId(), t.getTransactionType(), t.getAmount(), t.getCardId());
        }

        int totalSpent = 0;
        int totalIncome = 0;

        List<Map<String, Object>> transactionsMap = new ArrayList<>();

        for (Transaction t : transactions) {
            try {
                int amount = t.getAmount();

                // 거래 유형을 다양하게 확인 (카드 거래는 일반적으로 지출)
                Integer type = t.getTransactionType();

                // 타입이 1이면 입금(수입), 그 외는 모두 지출로 처리
                boolean isIncome = (type != null && type == 1);

                if (isIncome) {
                    totalIncome += amount;
                } else {
                    totalSpent += amount;
                }

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transactionId", t.getTransactionId());
                transaction.put("date", t.getTransactionDate().toLocalDate().toString());
                transaction.put("categoryId", t.getCategoryId());
                transaction.put("categoryName", getCategoryName(t.getCategoryId()));
                transaction.put("merchantName", t.getMerchantName());
                transaction.put("amount", amount);
                transaction.put("transactionType", isIncome ? "INCOME" : "EXPENSE");
                transaction.put("is_waste", t.getIsWaste());
                transaction.put("description", t.getMerchantName());

                transactionsMap.add(transaction);
            } catch (Exception e) {
                log.error("거래 내역 처리 중 오류: {}", t.getTransactionId(), e);
            }
        }

        // 집계 결과 로깅
        log.info("월 합계 - 수입: {}, 지출: {}", totalIncome, totalSpent);

        return PaymentHistoryResponseDto.builder()
                .year(year)
                .month(month)
                .totalSpent(totalSpent)
                .totalIncome(totalIncome)
                .transactionsMap(transactionsMap)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDailyConsumption(
            String email,
            String date
    ) {
        if ("anonymousUser".equals(email)) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        Integer userId = user.getUserId();

        LocalDate targetDate = LocalDate.parse(date);
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

        // transactions 테이블에서 조회
        List<Transaction> dailyTransactions = transactionRepository
                .findByUserIdAndTransactionDateBetween(userId, startOfDay, endOfDay);

        int totalSpent = 0;
        int totalIncome = 0;

        List<Map<String, Object>> transactions = new ArrayList<>();

        for (Transaction t : dailyTransactions) {
            try {
                int amount = t.getAmount();

                // 입금/출금 구분
                if (t.getTransactionType() == 1) { // 입금
                    totalIncome += amount;
                } else { // 출금
                    totalSpent += amount;
                }

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transactionId", t.getTransactionId());
                transaction.put("categoryId", t.getCategoryId());
                transaction.put("category", getCategoryName(t.getCategoryId()));
                transaction.put("merchantName", t.getMerchantName());
                transaction.put("description", t.getMerchantName());
                transaction.put("amount", amount);
                transaction.put("transactionType", t.getTransactionType());
                transaction.put("is_waste", t.getIsWaste());

                transactions.add(transaction);
            } catch (Exception e) {
                log.error("거래 내역 처리 중 오류: {}", t.getTransactionId(), e);
            }
        }

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
    public Integer toggleWasteStatus(Integer transactionId) {
        // PaymentHistory가 아닌 Transaction으로 변경
        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() -> new RuntimeException("거래 내역을 찾을 수 없습니다."));

        Integer currentWasteStatus = transaction.getIsWaste();
        transaction.setIsWaste(currentWasteStatus == null || currentWasteStatus == 0 ? 1 : 0);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return updatedTransaction.getIsWaste();
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

        // 사용자의 예산 정보 조회
        List<BudgetEntity> budgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        int goalAmount = budgets.stream()
                .mapToInt(budget -> budget.getAmount() != null ? budget.getAmount() : 0)
                .sum();

        // 결제 내역 조회
        List<PaymentHistoryEntity> payments = paymentHistoryRepository
                .findByCardIdInAndTransactionDateBetween(userCardIds, startDate, endDate);

        // 거래 내역 조회 (Transaction 테이블)
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                user.getUserId(),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 모든 카테고리 정보 조회
        Map<Integer, String> budgetCategoryMap = new HashMap<>();
        List<BudgetCategoryEntity> allBudgetCategories = budgetCategoryRepository.findAll();
        for (BudgetCategoryEntity bc : allBudgetCategories) {
            budgetCategoryMap.put(bc.getBudgetCategoryId(), bc.getCategoryName());
        }

        // 카테고리별 금액 집계
        Map<String, Integer> categoryAmounts = new HashMap<>();

        // 기본 카테고리 먼저 추가 (모든 예산 카테고리 포함)
        for (BudgetCategoryEntity category : allBudgetCategories) {
            categoryAmounts.put(category.getCategoryName(), 0);
        }

        // 추가로 기타 카테고리 추가
//        categoryAmounts.put("기타", 0);

        int totalSpent = 0;

        // PaymentHistory 테이블 데이터 처리
        for (PaymentHistoryEntity payment : payments) {
            try {
                String categoryIdStr = payment.getCategoryId();
                Integer categoryId = null;
                String merchantName = payment.getMerchantName();

                try {
                    categoryId = Integer.parseInt(categoryIdStr);
                } catch (NumberFormatException e) {
                    log.warn("카테고리 ID를 파싱할 수 없습니다: {}", categoryIdStr);
                    continue;
                }

                int amount = Math.abs(Integer.parseInt(payment.getTransactionBalance().trim().replace(",", "")));
                totalSpent += amount;

                // 기본값으로 "기타" 설정
                String budgetCategoryName = "결제";

                // 카테고리 매핑 시도
                try {
                    // 1. 가맹점 이름으로 category 테이블 조회
                    if (merchantName != null && !merchantName.isEmpty()) {
                        List<CategoryEntity> matchingCategories = categoryRepository.findByMerchantName(merchantName);
                        if (!matchingCategories.isEmpty() && matchingCategories.get(0).getBudgetCategory() != null) {
                            Integer budgetCategoryId = matchingCategories.get(0).getBudgetCategory();
                            String mappedName = budgetCategoryMap.get(budgetCategoryId);
                            if (mappedName != null) {
                                budgetCategoryName = mappedName;
                            }
                        }
                    }

                    // 2. 카테고리 ID로 조회 (가맹점으로 찾지 못한 경우)
                    if (budgetCategoryName.equals("결제") && categoryId != null) {
                        CategoryEntity category = categoryRepository.findById(categoryId).orElse(null);
                        if (category != null && category.getBudgetCategory() != null) {
                            Integer budgetCategoryId = category.getBudgetCategory();
                            String mappedName = budgetCategoryMap.get(budgetCategoryId);
                            if (mappedName != null) {
                                budgetCategoryName = mappedName;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("카테고리 매핑 중 오류 발생: {}", e.getMessage());
                }

                // 카테고리별 금액 누적
                categoryAmounts.put(budgetCategoryName,
                        categoryAmounts.getOrDefault(budgetCategoryName, 0) + amount);

            } catch (Exception e) {
                log.error("결제 내역 처리 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        // Transaction 테이블 데이터 처리
        for (Transaction transaction : transactions) {
            try {
                if (transaction.getTransactionType() != 1) { // 출금(지출)만 처리
                    int amount = transaction.getAmount();
                    Integer categoryId = transaction.getCategoryId();
                    String budgetCategoryName = "결제"; // 기본값을 "결제"로 변경

                    log.info("거래 내역 로그:");
                    log.info("거래 ID: {}", transaction.getTransactionId());
                    log.info("카테고리 ID: {}", categoryId);
                    log.info("가맹점명: {}", transaction.getMerchantName());
                    log.info("금액: {}", amount);

                    // 카테고리 매핑 시도
                    if (categoryId != null) {
                        // 직접 budget_category_name을 찾아 매핑
                        Optional<BudgetCategoryEntity> budgetCategory = budgetCategoryRepository.findById(categoryId);

                        log.info("BudgetCategory 조회 결과:");
                        if (budgetCategory.isPresent()) {
                            budgetCategoryName = budgetCategory.get().getCategoryName();
                            log.info("매핑된 예산 카테고리명: {}", budgetCategoryName);
                        }
                    }

                    // 카테고리별 금액 누적
                    categoryAmounts.put(budgetCategoryName,
                            categoryAmounts.getOrDefault(budgetCategoryName, 0) + amount);
                    totalSpent += amount;
                }
            } catch (Exception e) {
                log.error("거래 내역 처리 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        // 집계 결과 변환
        List<CategoryStatisticsResponseDto.CategoryData> categories = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryAmounts.entrySet()) {
            // 금액이 0인 카테고리도 포함
            double percentage = totalSpent > 0 ? (entry.getValue() * 100.0) / totalSpent : 0;

            categories.add(CategoryStatisticsResponseDto.CategoryData.builder()
                    .name(entry.getKey())
                    .amount(entry.getValue())
                    .percentage(Math.round(percentage * 10) / 10.0) // 소수점 첫째 자리까지
                    .build());
        }

        // 금액 내림차순 정렬
        categories.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));

        // 금액이 0인 카테고리 필터링 (선택적)
        // categories = categories.stream().filter(c -> c.getAmount() > 0).collect(Collectors.toList());

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