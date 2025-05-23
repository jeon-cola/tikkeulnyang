package com.c107.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    // 공통 status 상수
    public static final String SUCCESS_STATUS = "success";
    public static final String FAIL_STATUS = "fail";
    public static final String ERROR_STATUS = "error";

    // 응답 빌더 메서드
    public static ResponseEntity<Map<String, Object>> buildResponse(String message, Object data, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        // 상태는 2xx이면 success, 4xx이면 fail, 5xx이면 error로 구분
        if (status.is2xxSuccessful()) {
            response.put("status", SUCCESS_STATUS);
        } else if (status.is5xxServerError()) {
            response.put("status", ERROR_STATUS);
        } else {
            response.put("status", FAIL_STATUS);
        }
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, status);
    }

    // 200 OK: 성공 응답
    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        return buildResponse(message, data, HttpStatus.OK);
    }

    // 400 Bad Request: 요청 실패
    public static ResponseEntity<Map<String, Object>> badRequest(String message, Object data) {
        return buildResponse(message, data, HttpStatus.BAD_REQUEST);
    }

    // 401 Unauthorized: 인증 실패
    public static ResponseEntity<Map<String, Object>> unauthorized(String message, Object data) {
        return buildResponse(message, data, HttpStatus.UNAUTHORIZED);
    }

    // 403 Forbidden: 권한 없음
    public static ResponseEntity<Map<String, Object>> forbidden(String message, Object data) {
        return buildResponse(message, data, HttpStatus.FORBIDDEN);
    }

    // 500 Internal Server Error: 서버 내부 오류
    public static ResponseEntity<Map<String, Object>> serverError(String message, Object data) {
        return buildResponse(message, data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 오버로드: 사용자 지정 HTTP 상태 코드 사용
    public static ResponseEntity<Map<String, Object>> serverError(String message, Object data, HttpStatus status) {
        return buildResponse(message, data, status);
    }
}
