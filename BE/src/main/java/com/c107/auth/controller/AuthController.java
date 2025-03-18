package com.c107.auth.controller;

import com.c107.auth.service.AuthService;
import com.c107.common.util.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인:
     * - 들어오면 바로 카카오 로그인 페이지로 서버에서 302 리다이렉트
     */
    @GetMapping("/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        authService.redirectToKakaoLogin(response);
    }

    /**
     * 카카오 로그인 후 콜백 (인가 코드 수신 → JWT 발급 혹은 신규회원 안내)
     */
    @GetMapping("/callback")
    public void kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) throws IOException {
        // authenticateWithKakao 내부에서 사용자 확인 후, 리다이렉트 URL을 결정하도록 함.
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

}
