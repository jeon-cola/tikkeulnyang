package com.c107.recommendcard.dto;

import com.c107.recommendcard.entity.RecommendCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor // ✅ Redis 캐싱 역직렬화를 위해 꼭 필요
@AllArgsConstructor // Builder를 사용했으니 같이 추가
public class RecommendCardResponseDto {
    private Long recoCardId;
    private String recoCardName;
    private String cardType;
    private String corpName;
    private String imagePath;
    private String benefitDescription; // 예시로 혜택 설명

    public static RecommendCardResponseDto of(RecommendCard card, String benefitDescription) {
        return RecommendCardResponseDto.builder()
                .recoCardId(card.getRecoCardId())
                .recoCardName(card.getRecoCardName())
                .cardType(card.getCardType())
                .corpName(card.getCorpName())
                .imagePath(card.getImagePath())
                .benefitDescription(benefitDescription)
                .build();
    }
}
