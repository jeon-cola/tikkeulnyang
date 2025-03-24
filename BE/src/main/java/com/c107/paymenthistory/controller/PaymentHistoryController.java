package com.c107.paymenthistory.controller;

import com.c107.paymenthistory.dto.CategoryStatisticsResponseDto;
import com.c107.paymenthistory.dto.PaymentHistoryResponseDto;
import com.c107.paymenthistory.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.c107.common.util.ResponseUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false, defaultValue = "personal") String type
    ) {
        // 년/월 파라미터가 없으면 현재 년/월로 설정
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        PaymentHistoryResponseDto responseDto = paymentHistoryService.getConsumptionCalendar(
                email, targetYear, targetMonth, type);

        return ResponseUtil.success("소비 내역 조회에 성공했습니다.", responseDto);
    }

    @GetMapping("/consumption/monthly")
    public ResponseEntity<?> getMonthlyConsumption(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        PaymentHistoryResponseDto responseDto = paymentHistoryService.getMonthlyConsumption(
                email, targetYear, targetMonth);

        return ResponseUtil.success("월별 소비 내역 조회에 성공했습니다.", responseDto);

//        try {
//            LocalDate now = LocalDate.now();
//            int targetYear = (year != null) ? year : now.getYear();
//            int targetMonth = (month != null) ? month : now.getMonthValue();
//
//            PaymentHistoryResponseDto responseDto = paymentHistoryService.getMonthlyConsumption(
//                    email, targetYear, targetMonth);
//
//            return ResponseUtil.success("월별 소비 내역 조회에 성공했습니다.", responseDto);
//        } catch (Exception e) {
//            log.error("월별 소비 내역 조회 중 오류 발생", e);
//            return ResponseUtil.error("월별 소비 내역 조회에 실패했습니다.");
//        }
    }

    @GetMapping("/consumption/daily/{date}")
    public ResponseEntity<?> getDailyConsumption(
            @AuthenticationPrincipal String email,
            @PathVariable String date
    ) {

        PaymentHistoryResponseDto responseDto = paymentHistoryService.getDailyConsumption(
                email, date);

        return ResponseUtil.success("일별 소비 내역 조회에 성공했습니다.", responseDto);
    }

    @PostMapping("/waste")
    public ResponseEntity<?> toggleWasteStatus(
            @RequestBody PaymentHistoryResponseDto.WasteToggleRequest request
    ) {
        Integer isWaste = paymentHistoryService.toggleWasteStatus(request.getPaymentHistoryId());

        Map<String, Integer> response = new HashMap<>();
        response.put("is_waste", isWaste);

        return ResponseUtil.success("낭비 소비 상태가 성공적으로 변경되었습니다.", response);
    }

    @GetMapping("/statistics/category")
    public ResponseEntity<?> getCategoryStatistics(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        // 기본값으로 현재 연월 사용
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        CategoryStatisticsResponseDto responseDto =
                paymentHistoryService.getCategoryStatistics(email, targetYear, targetMonth);

        return ResponseUtil.success("카테고리별 소비 통계 조회에 성공했습니다.", responseDto);
    }

    @GetMapping("/statistics/category/{year}/{month}")
    public ResponseEntity<?> getCategoryStatistics(
            @AuthenticationPrincipal String email,
            @PathVariable int year,
            @PathVariable int month
    ) {
        CategoryStatisticsResponseDto responseDto =
                paymentHistoryService.getCategoryStatistics(email, year, month);

        return ResponseUtil.success("카테고리별 소비 통계 조회에 성공했습니다.", responseDto);
    }

}