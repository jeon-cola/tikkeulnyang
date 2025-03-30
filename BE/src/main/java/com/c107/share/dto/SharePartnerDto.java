package com.c107.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharePartnerDto {
    private Long userId;           // 공유 대상 유저 ID
    private String nickname;       // 닉네임
    private String profileImageUrl; // 프로필 이미지 URL (없을 수도 있음)
}

