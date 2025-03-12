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

    /**
     * 가계부 캘린더 조회 API - 일별 지출 내역 제공
     */
    @GetMapping("/consumption")
    public ResponseEntity<?> getConsumptionCalendar(
            @RequestParam(required = false, defaultValue = "101") Integer userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        try {
            // 년/월 파라미터가 없으면 현재 년/월로 설정
            LocalDate now = LocalDate.now();
            int targetYear = (year != null) ? year : now.getYear();
            int targetMonth = (month != null) ? month : now.getMonthValue();

            PaymentHistoryResponseDto responseDto = paymentHistoryService.getConsumptionCalendar(
                    userId, targetYear, targetMonth);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 테스트용 더미 데이터 생성 API
     */
//    @PostMapping("/dummy")
//    public ResponseEntity<?> createDummyData(
//            @RequestParam(required = false, defaultValue = "101") Integer userId,
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) Integer month) {
//
//        log.info("더미 데이터 생성 요청: userId={}, year={}, month={}", userId, year, month);
//
//        try {
//            // 년/월 파라미터가 없으면 현재 년/월로 설정
//            LocalDate now = LocalDate.now();
//            int targetYear = (year != null) ? year : now.getYear();
//            int targetMonth = (month != null) ? month : now.getMonthValue();
//
//            paymentHistoryService.createDummyData(userId, targetYear, targetMonth);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "더미 데이터 생성이 완료되었습니다.");
//            response.put("userId", userId);
//            response.put("year", targetYear);
//            response.put("month", targetMonth);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("더미 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "더미 데이터 생성에 실패했습니다.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    /**
     * 낭비 소비 표시 업데이트 API
     */
//    @PutMapping("/waste/{paymentHistoryId}")
//    public ResponseEntity<?> updateWasteFlag(
//            @PathVariable Integer paymentHistoryId,
//            @RequestParam(required = false, defaultValue = "true") Boolean isWaste) {
//
//        log.info("낭비 소비 표시 업데이트 요청: paymentHistoryId={}, isWaste={}", paymentHistoryId, isWaste);
//
//        try {
//            paymentHistoryService.updateWasteFlag(paymentHistoryId, isWaste);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "낭비 소비 표시가 업데이트되었습니다.");
//            response.put("paymentHistoryId", paymentHistoryId);
//            response.put("isWaste", isWaste);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("낭비 소비 표시 업데이트 중 오류 발생: {}", e.getMessage(), e);
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "낭비 소비 표시 업데이트에 실패했습니다.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
}