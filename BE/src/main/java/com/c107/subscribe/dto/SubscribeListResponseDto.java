package com.c107.subscribe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeListResponseDto {

    private String status;
    private String message;
    private List<SubscribeResponseDto> subscriptions;

    public static SubscribeListResponseDto forPaymentDateOrder(List<SubscribeResponseDto> subscriptions) {
        // 남은 일수(daysRemaining)를 기준으로 오름차순 정렬
        List<SubscribeResponseDto> sortedSubscriptions = subscriptions.stream()
                .sorted(Comparator.comparing(SubscribeResponseDto::getDaysRemaining))
                .collect(Collectors.toList());

        return SubscribeListResponseDto.builder()
                .subscriptions(sortedSubscriptions)
                .build();
    }

    public static SubscribeListResponseDto forPriceOrder(List<SubscribeResponseDto> subscriptions) {
        return SubscribeListResponseDto.builder()
                .subscriptions(subscriptions)
                .build();
    }
}