package com.c107.recommendcard.controller;

import com.c107.recommendcard.dto.RecommendCardResponseDto;
import com.c107.recommendcard.service.RecommendCardService;
import com.c107.common.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend/cards")
@RequiredArgsConstructor
@Slf4j
public class RecommendCardController {

    private final RecommendCardService recommendCardService;

    /**
     * 체크카드 추천 조회 엔드포인트
     * URL 예시: GET /api/recommend/cards/check
     */
    @GetMapping("/check")
    public ResponseEntity<?> getRecommendedCheckCards(@AuthenticationPrincipal String email) {
        try {
            List<RecommendCardResponseDto> recommendedCards = recommendCardService.recommendCheckCards(email);
            return ResponseUtil.success("체크카드 추천 조회에 성공했습니다.", recommendedCards);
        } catch (Exception e) {
            log.error("체크카드 추천 조회 중 오류 발생", e);
            return ResponseUtil.badRequest("체크카드 추천 조회에 실패했습니다.",null);
        }
    }

    /**
     * 신용카드 추천 조회 엔드포인트
     * URL 예시: GET /api/recommend/cards/credit
     */
    @GetMapping("/credit")
    public ResponseEntity<?> getRecommendedCreditCards(@AuthenticationPrincipal String email) {
        try {
            List<RecommendCardResponseDto> recommendedCards = recommendCardService.recommendCreditCards(email);
            return ResponseUtil.success("신용카드 추천 조회에 성공했습니다.", recommendedCards);
        } catch (Exception e) {
            log.error("신용카드 추천 조회 중 오류 발생", e);
            return ResponseUtil.badRequest("신용카드 추천 조회에 실패했습니다.",null);
        }
    }
}
