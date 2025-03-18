package com.c107.subscribe.dto;

import com.c107.subscribe.entity.SubscribeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeResponseDto {
    private String email;
    private Integer subscribeId;
    private Integer userId;
    private String subscribeName;
    private Integer subscribePrice;
    private String paymentDate;
    private Integer daysRemaining;

    public static SubscribeResponseDto fromEntity(SubscribeEntity entity) {
        // 현재 연도/월 기준으로 결제일을 문자열로 변환
        YearMonth currentYearMonth = YearMonth.now();
        int paymentDay = entity.getPaymentDate();
        LocalDate today = LocalDate.now();

        // 결제일 계산
        LocalDate paymentDate = currentYearMonth.atDay(paymentDay);

        // 만약 이번 달의 결제일이 이미 지났다면, 다음 달의 결제일로 계산
        if (paymentDate.isBefore(today) || paymentDate.isEqual(today)) {
            paymentDate = currentYearMonth.plusMonths(1).atDay(paymentDay);
        }

        // 남은 일수 계산 (오늘 제외하고 계산)
        int daysRemaining = (int) ChronoUnit.DAYS.between(today, paymentDate);

        String formattedDate = paymentDate.toString(); // yyyy-MM-dd 형식

        return SubscribeResponseDto.builder()
                .subscribeId(entity.getSubscribeId())
//                .userId(entity.getUserId())
                .email(entity.getEmail())
                .subscribeName(entity.getSubscribeName())
                .subscribePrice(entity.getSubscribePrice())
                .paymentDate(formattedDate)
                .daysRemaining(daysRemaining)
                .build();
    }


}