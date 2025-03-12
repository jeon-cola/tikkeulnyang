package com.c107.paymenthistory.service;

import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.entity.CardEntity;
import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import com.c107.paymenthistory.repository.CardRepository;
import com.c107.paymenthistory.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CardRepository cardRepository;

    /**
     * 가계부 캘린더 조회 - 일별 지출 내역 제공
     */
    @Transactional(readOnly = true)
    public PaymentHistoryResponseDto getConsumptionCalendar(Integer userId, int year, int month) {
        // 해당 월의 첫날과 마지막날 구하기
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 사용자의 해당 월 결제 내역 조회 (JOIN을 통해)
        List<PaymentHistoryEntity> paymentHistories = paymentHistoryRepository.findByUserIdAndTransactionDateBetween(
                userId, startDate, endDate);

        // 날짜별 결제 내역 그룹화
        Map<LocalDate, List<PaymentHistoryEntity>> paymentsByDate = paymentHistories.stream()
                .collect(Collectors.groupingBy(PaymentHistoryEntity::getTransactionDate));

        // 일별 데이터 생성
        List<PaymentHistoryResponseDto.DayData> dayDataList = new ArrayList<>();

        // 해당 월의 모든 날짜에 대한 데이터 생성
        for (int day = 1; day <= endDate.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            List<PaymentHistoryEntity> dayPayments = paymentsByDate.getOrDefault(date, Collections.emptyList());

            // 지출 계산 (Double 대신 Integer 사용)
            int spending = dayPayments.stream()
                    .mapToInt(p -> -1 * Integer.parseInt(p.getTransactionBalance())) // 지출은 음수로 표현, int로 변환
                    .sum();

            dayDataList.add(PaymentHistoryResponseDto.DayData.builder()
                    .date(date.format(DateTimeFormatter.ISO_DATE))
                    .spending(spending) // Integer 타입으로 저장
                    .build());
        }

        return PaymentHistoryResponseDto.builder()
                .year(year)
                .month(month)
                .data(dayDataList)
                .build();
    }

    // 나머지 메서드는 동일하게 유지...
}