package com.c107.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {
    private String name;
    private String nickname;
    private String birthDate;
}

