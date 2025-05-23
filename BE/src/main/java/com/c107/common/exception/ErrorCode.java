package com.c107.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED("입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("요청한 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
