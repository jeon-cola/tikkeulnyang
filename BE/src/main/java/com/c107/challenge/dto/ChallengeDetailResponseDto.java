package com.c107.challenge.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChallengeDetailResponseDto {
    // 챌린지 기본 정보
    private ChallengeResponseDto challenge;
    // 참가자 수
    private int participantCount;
    // 성공률 구간별 분포
    private int bucket100to85;
    private int bucket84to50;
    private int bucket49to25;
    private int bucket24to0;
    // 전체 참가자 평균 성공률
    private double averageSuccessRate;
    private String thumbnailUrl;
}
