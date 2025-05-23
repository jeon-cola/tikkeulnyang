package com.c107.cards.dto;

import com.c107.cards.entity.CardInfoEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseDto {

    @JsonProperty("cards")
    private List<CardInfo> cards;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CardInfo {
        @JsonProperty("card_id")
        private Integer cardId;


        @JsonProperty("card_name")
        private String cardName;

        @JsonProperty("card_no")
        private String cardNo;

        @JsonProperty("card_type")
        private String cardType;

        @JsonProperty("created_at")
        private String createdAt;

        private String imagePath; // 추가
        private List<String> benefits; // 추가

        public static CardInfo fromEntity(CardInfoEntity entity) {
            return CardInfo.builder()
                    .cardId(entity.getCardId())
                    .cardName(entity.getCardName())
                    .cardNo(entity.getCardNo())
                    .cardType(entity.getCardType())
                    .createdAt(entity.getCreatedAt().toString())
                    .imagePath(null) // 초기값 null
                    .benefits(new ArrayList<>()) // 초기값 빈 리스트
                    .build();
        }
    }
}