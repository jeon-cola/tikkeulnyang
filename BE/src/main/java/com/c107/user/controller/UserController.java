package com.c107.user.controller;

import com.c107.auth.entity.LoginUserEntity;
import com.c107.auth.repository.LoginUserRepository;
import com.c107.common.util.JwtUtil;
import com.c107.common.util.ResponseUtil;
import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginUserRepository loginUserRepository; // 추가
    private final JwtUtil  jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        Optional<LoginUserEntity> existingUser = loginUserRepository.findByNickname(nickname);
        if (existingUser.isPresent()) {
            // 닉네임 중복
            return ResponseUtil.badRequest("사용중인 닉네임입니다.", null);
        } else {
            // 사용 가능
            return ResponseUtil.success("닉네임 사용 가능", null);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "accessToken", required = false) String accessToken,
                                            HttpServletResponse response) {
        if (accessToken == null) {
            return ResponseUtil.badRequest("Access 토큰 쿠키가 존재하지 않습니다.", null);
        }

        String email;
        try {
            email = jwtUtil.parseClaims(accessToken).getSubject();
        } catch (Exception e) {
            return ResponseUtil.badRequest("토큰 검증에 실패하였습니다.", null);
        }

        if (email == null) {
            return ResponseUtil.badRequest("토큰에서 이메일을 추출할 수 없습니다.", null);
        }

        Optional<LoginUserEntity> userOpt = loginUserRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseUtil.badRequest("사용자 정보를 찾을 수 없습니다.", null);
        }
        LoginUserEntity user = userOpt.get();

        // (선택 사항) 응답 후 Access Token 쿠키 삭제 (재사용 방지)
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        // 필요시, 사용자 ID나 원하는 정보를 JSON으로 반환
        Map<String, Object> responseBody = Map.of("id", user.getUserId(), "email", user.getEmail(), "nickname", user.getNickname());
        return ResponseEntity.ok(ResponseUtil.success("현재 사용자 정보", responseBody));
    }
}
