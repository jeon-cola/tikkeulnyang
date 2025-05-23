package com.c107.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareLedgerResponseDto {
    private int year;
    private int month;
    private int totalIncome;
    private int totalSpent;
    private int totalBudget;
    private List<ShareLedgerResponseDto.DailyData> data;
    private String ownerNickname;
    private String ownerEmail;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyData {
        private String date;
        private int emoji; // 0: 예산 미만, 1: 예산과 같음, 2: 예산 초과
    }
}