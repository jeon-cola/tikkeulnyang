package com.c107.auth.controller;

import com.c107.auth.service.AuthService;
import com.c107.common.util.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    /**
     * 카카오 로그인:
     * - 들어오면 바로 카카오 로그인 페이지로 서버에서 302 리다이렉트
     */
    @GetMapping("/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        authService.redirectToKakaoLogin(response);
    }

    //
    //
    //
    //
    //
    //
    /**
     * 카카오 로그인 후 콜백 (인가 코드 수신 → JWT 발급 혹은 신규회원 안내)
     */
    @GetMapping("/callback")
    public void kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) throws IOException {
        // authenticateWithKakao 내부에서 사용자 확인 후, 리다이렉트 URL을 결정하도록 함.
        logger.debug("api콜백은 찍힘");
        authService.authenticateWithKakaoAndRedirect(code, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            Principal principal,
            HttpServletResponse response) {
        if (principal == null) {
            return ResponseUtil.badRequest("유저를 찾을 수 없습니다.", null);
        }

        String email = principal.getName(); // Principal에서 이메일 추출
        return authService.logout(response, email);
    }

    @GetMapping("/callback/json")
    public ResponseEntity<?> kakaoCallbackJson(@RequestParam("code") String code) {
        return authService.authenticateWithKakaoAndReturnJson(code);
    }



    // 로그인 실패 메서드
    @PostMapping("/simulate-login-failure")
    public ResponseEntity<?> simulateLoginFailure(
            @RequestParam String email,
            @RequestParam String ipAddress,
            @RequestParam String userAgent
    ) {
        // 로그인 실패 이벤트 시뮬레이션
        Map<String, Object> failureLog = new HashMap<>();
        failureLog.put("email", email);
        failureLog.put("ip", ipAddress);
        failureLog.put("user_agent", userAgent);

        // 로그인 실패 이벤트 기록
        authService.recordLoginFailure(failureLog);

        return ResponseEntity.ok("Login failure simulated");
    }


}
