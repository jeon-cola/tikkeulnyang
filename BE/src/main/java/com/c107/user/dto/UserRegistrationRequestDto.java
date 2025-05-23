package com.c107.user.dto;

import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    private String email;
    private String nickname;    // 신규 입력한 닉네임 (변경 가능)
    private String name;        // 추가로 받는 이름
    private String birthDate;   // 생년월일 (예: "1990-01-01")
}
