package com.c107.cards.controller;

import com.c107.cards.dto.CardResponseDto;
import com.c107.cards.dto.CardTransactionResponseDto;
import com.c107.cards.service.CardService;
import com.c107.common.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // 금융api 카드 정보 갱신
    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshCards(@AuthenticationPrincipal String email) {
        CardResponseDto responseDto = cardService.refreshCards(email);
        return ResponseUtil.success("카드 정보가 성공적으로 갱신되었습니다.", responseDto);
    }

    // 사용자 카드목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserCards(@AuthenticationPrincipal String email) {
        CardResponseDto responseDto = cardService.getUserCards(email);
        return ResponseUtil.success("카드 목록 조회에 성공했습니다.", responseDto);
    }

    //  특정 카드 거래내역 조회
    @GetMapping("/{cardNo}/transactions")
    public ResponseEntity<Map<String, Object>> getCardTransactions(
            @AuthenticationPrincipal String email,
            @PathVariable String cardNo,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        // 기본값으로 현재 연월 사용
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        CardTransactionResponseDto responseDto =
                cardService.getCardTransactions(email, cardNo, targetYear, targetMonth);

        return ResponseUtil.success("카드 거래내역 조회에 성공했습니다.", responseDto);
    }

    // 사용자의 모든 카드 거래내역 조회
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getAllCardTransactions(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        // 기본값으로 현재 연월 사용
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        CardTransactionResponseDto responseDto =
                cardService.getAllCardTransactions(email, targetYear, targetMonth);

        return ResponseUtil.success("모든 카드 거래내역 조회에 성공했습니다.", responseDto);
    }
}
