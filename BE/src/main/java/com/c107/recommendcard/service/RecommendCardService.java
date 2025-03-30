package com.c107.recommendcard.service;

import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.recommendcard.entity.RecommendCard;
import com.c107.recommendcard.repository.RecommendCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendCardService {

    private final RecommendCardRepository recommendCardRepository;

    public List<RecommendCard> getAllRecommendedCards() {
        return recommendCardRepository.findAll();
    }

    public List<RecommendCard> getRecommendedCardsByType(String cardType) {
        return recommendCardRepository.findByCardType(cardType);
    }

    public RecommendCard getRecommendCardById(Integer id) {
        return recommendCardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "추천 카드 정보를 찾을 수 없습니다."));
    }
}
