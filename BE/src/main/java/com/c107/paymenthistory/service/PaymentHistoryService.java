package com.c107.paymenthistory.service;

import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.entity.CardEntity;
import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import com.c107.paymenthistory.repository.CardRepository;
import com.c107.paymenthistory.repository.PaymentHistoryRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        int totalSpent = calculateTotalAmount(paymentHistories, false);
        int totalIncome = calculateTotalAmount(paymentHistories, true);

        List<PaymentHistoryResponseDto.Transaction> transactions = paymentHistories.stream()
                .map(p -> PaymentHistoryResponseDto.Transaction.builder()
                        .date(p.getTransactionDate().toString())
                        .categoryName(p.getCategoryName())
                        .merchantName(p.getMerchantName())
                        .transactionBalance(Math.abs(Integer.parseInt(p.getTransactionBalance())))
                        .build())
                .collect(Collectors.toList());

        return PaymentHistoryResponseDto.builder()
                .year(year)
                .month(month)
                .totalSpent(totalSpent)
                .totalIncome(totalIncome)
                .transactions(transactions)
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentHistoryResponseDto getDailyConsumption(
            String email,
            String date
    ) {
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
        int totalIncome = calculateTotalAmount(paymentHistories, true);

        List<PaymentHistoryResponseDto.Transaction> transactions = paymentHistories.stream()
                .map(p -> PaymentHistoryResponseDto.Transaction.builder()
                        .category(p.getCategoryName())
                        .amount(-Math.abs(Integer.parseInt(p.getTransactionBalance())))
                        .description(p.getMerchantName())
                        .build())
                .collect(Collectors.toList());

        return PaymentHistoryResponseDto.builder()
                .date(date)
                .totalSpent(totalSpent)
                .totalIncome(totalIncome)
                .transactions(transactions)
                .build();
    }

    private int calculateTotalAmount(List<PaymentHistoryEntity> paymentHistories, boolean isIncome) {
        return paymentHistories.stream()
                .filter(p -> isIncome ?
                        Integer.parseInt(p.getTransactionBalance()) > 0 :
                        Integer.parseInt(p.getTransactionBalance()) < 0)
                .mapToInt(p -> Math.abs(Integer.parseInt(p.getTransactionBalance())))
                .sum();
    }
}