package com.c107.common.exception;

import com.c107.common.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 401 Unauthorized 예외 처리 (인증 실패)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(org.springframework.security.core.AuthenticationException ex) {
        logger.error("AuthenticationException 발생", ex);
        return ResponseUtil.unauthorized("인증이 필요합니다.", null);
    }

    // 403 Forbidden 예외 처리 (권한 없음)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(AccessDeniedException ex) {
        logger.error("AccessDeniedException 발생", ex);
        return ResponseUtil.forbidden("접근 권한이 없습니다.", null);
    }

    // 400 Bad Request 예외 처리 (잘못된 요청)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequestException(Exception ex) {
        logger.error("BadRequestException 발생", ex);
        Map<String, Object> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
        }
        return ResponseUtil.badRequest("잘못된 요청입니다.", errors.isEmpty() ? null : errors);
    }

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        logger.error("CustomException 발생: {}", ex.getMessage(), ex);
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseUtil.serverError(errorCode.getMessage(), null, errorCode.getHttpStatus());
    }

    // 그 외 모든 Exception 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        logger.error("Unhandled Exception 발생", ex);
        return ResponseUtil.serverError("서버 내부 오류가 발생했습니다.", null);
    }
}
