package com.c107.recommendcard.controller;

import com.c107.recommendcard.entity.RecommendCard;
import com.c107.recommendcard.service.RecommendCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommend-cards")
@RequiredArgsConstructor
public class RecommendCardController {

    private final RecommendCardService recommendCardService;

    // 전체 추천 카드 조회 또는 카드 타입으로 필터링하여 조회
    @GetMapping
    public ResponseEntity<List<RecommendCard>> getRecommendedCards(
            @RequestParam(required = false) String cardType) {
        List<RecommendCard> cards;
        if (cardType != null && !cardType.isEmpty()) {
            cards = recommendCardService.getRecommendedCardsByType(cardType);
        } else {
            cards = recommendCardService.getAllRecommendedCards();
        }
        return ResponseEntity.ok(cards);
    }

    // 특정 추천 카드 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<RecommendCard> getRecommendCardById(@PathVariable Integer id) {
        RecommendCard card = recommendCardService.getRecommendCardById(id);
        return ResponseEntity.ok(card);
    }
}
