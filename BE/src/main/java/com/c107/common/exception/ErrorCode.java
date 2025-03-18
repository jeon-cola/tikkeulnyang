package com.c107.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED("입력값이 유효하지 않습니다."),
    UNAUTHORIZED("권한이 없습니다."),
    NOT_FOUND("요청한 정보를 찾을 수 없습니다.");

    private final String message;
}
