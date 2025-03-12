package com.c107.paymenthistory.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentHistoryResponseDto {

    private Integer year;
    private Integer month;
    private List<DayData> data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DayData {
        private String date;
        private Integer spending; // 지출 (음수로 표현)
    }
}