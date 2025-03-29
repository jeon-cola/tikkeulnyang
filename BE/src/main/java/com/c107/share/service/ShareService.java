package com.c107.share.service;

import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.repository.BudgetRepository;
import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.service.PaymentHistoryService;
import com.c107.share.dto.ShareLedgerResponseDto;
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
public class ShareService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final PaymentHistoryService paymentHistoryService;

    @Transactional(readOnly = true)
    public ShareLedgerResponseDto getMyLedger(String email, Integer year, Integer month) {
        // 사용자 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        // 현재 날짜 정보로 기본값 설정
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

        // 1. 예산 정보 조회
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<BudgetEntity> budgets = budgetRepository.findByEmailAndStartDateAndEndDate(
                email, startDate, endDate);

        int totalBudget = budgets.stream()
                .mapToInt(budget -> budget.getAmount() != null ? budget.getAmount() : 0)
                .sum();

        // 2. 소비 내역 조회
        PaymentHistoryResponseDto paymentHistory = paymentHistoryService.getConsumptionCalendar(
                email, targetYear, targetMonth, "personal");

        int totalIncome = paymentHistory.getTotalIncome();
        int totalSpent = paymentHistory.getTotalSpent();

        // 3. 일별 예산 계산 (월 예산을 일별로 분배)
        int daysInMonth = yearMonth.lengthOfMonth();
        double dailyBudget = totalBudget / (double) daysInMonth;

        // 4. 일별 데이터 생성
        List<ShareLedgerResponseDto.DailyData> dailyDataList = new ArrayList<>();

        // 일별 실제 지출 합계 계산
        Map<String, Integer> dailySpentMap = new HashMap<>();

        if (paymentHistory.getData() != null) {
            for (PaymentHistoryResponseDto.DayData dayData : paymentHistory.getData()) {
                dailySpentMap.put(dayData.getDate(), dayData.getExpense());
            }
        }

        // 달력 데이터 생성
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(targetYear, targetMonth, day);
            String dateStr = date.format(DateTimeFormatter.ISO_DATE);

            int dailySpent = dailySpentMap.getOrDefault(dateStr, 0);

            // 이모지 결정 (0: 예산 미만, 1: 예산과 같음, 2: 예산 초과)
            int emoji;
            if (Math.abs(dailySpent - dailyBudget) < 0.01) {
                emoji = 1; // 예산과 동일
            } else if (dailySpent > dailyBudget) {
                emoji = 2; // 예산 초과
            } else {
                emoji = 0; // 예산 미만
            }

            ShareLedgerResponseDto.DailyData dailyData = ShareLedgerResponseDto.DailyData.builder()
                    .date(dateStr)
                    .emoji(emoji)
                    .build();
            dailyDataList.add(dailyData);
        }

        // 응답 생성
        return ShareLedgerResponseDto.builder()
                .year(targetYear)
                .month(targetMonth)
                .totalIncome(totalIncome)
                .totalSpent(totalSpent)
                .totalBudget(totalBudget)
                .data(dailyDataList)
                .build();
    }
}