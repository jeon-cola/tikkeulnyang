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

    // 200 OK: 성공 응답
    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", SUCCESS_STATUS);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 400 Bad Request: 요청 실패
    public static ResponseEntity<Map<String, Object>> badRequest(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", FAIL_STATUS);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 401 Unauthorized: 인증 실패
    public static ResponseEntity<Map<String, Object>> unauthorized(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", FAIL_STATUS);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 403 Forbidden: 권한 없음
    public static ResponseEntity<Map<String, Object>> forbidden(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", FAIL_STATUS);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 500 Internal Server Error: 서버 내부 오류
    public static ResponseEntity<Map<String, Object>> serverError(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", ERROR_STATUS);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
