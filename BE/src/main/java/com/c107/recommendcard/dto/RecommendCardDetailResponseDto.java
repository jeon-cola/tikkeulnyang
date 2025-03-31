package com.c107.recommendcard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendCardDetailResponseDto {
    private int recoCardId;
    private String cardName;
    private String cardType;
    private String corpName;
    private String imagePath;
    private List<CategoryBenefitDto> benefits;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryBenefitDto {
        private String category;
        private String description;
    }
}
