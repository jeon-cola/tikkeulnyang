package com.c107.paymenthistory.controller;

import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/consumption")
    public ResponseEntity<?> getConsumptionCalendar(
            @RequestParam(required = false, defaultValue = "1") Integer userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false, defaultValue = "personal") String type
    ) {
        try {
            // 년/월 파라미터가 없으면 현재 년/월로 설정
            LocalDate now = LocalDate.now();
            int targetYear = (year != null) ? year : now.getYear();
            int targetMonth = (month != null) ? month : now.getMonthValue();

            PaymentHistoryResponseDto responseDto = paymentHistoryService.getConsumptionCalendar(
                    userId, targetYear, targetMonth, type);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("소비 내역 조회 중 오류 발생", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "소비 내역 조회에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/consumption/monthly")
    public ResponseEntity<?> getMonthlyConsumption(
            @RequestParam(required = false, defaultValue = "1") Integer userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        try {
            LocalDate now = LocalDate.now();
            int targetYear = (year != null) ? year : now.getYear();
            int targetMonth = (month != null) ? month : now.getMonthValue();

            PaymentHistoryResponseDto responseDto = paymentHistoryService.getMonthlyConsumption(
                    userId, targetYear, targetMonth);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("월별 소비 내역 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/consumption/daily/{date}")
    public ResponseEntity<?> getDailyConsumption(
            @RequestParam(required = false, defaultValue = "1") Integer userId,
            @PathVariable String date
    ) {
        try {
            PaymentHistoryResponseDto responseDto = paymentHistoryService.getDailyConsumption(
                    userId, date);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("일별 소비 내역 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}