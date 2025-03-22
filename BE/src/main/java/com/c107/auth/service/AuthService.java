package com.c107.auth.service;

import com.c107.auth.dto.KakaoTokenResponseDto;
import com.c107.auth.entity.LoginUserEntity;
import com.c107.auth.repository.LoginUserRepository;
import com.c107.common.util.JwtUtil;
import com.c107.common.util.ResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginUserRepository loginUserRepository;
    private final JwtUtil jwtUtil;
    private final FinanceService financeService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    /**
     * [1] 카카오 로그인 페이지로 바로 리다이렉트
     */
    public void redirectToKakaoLogin(HttpServletResponse response) throws IOException {
        String loginUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&response_type=code";
        response.sendRedirect(loginUrl);
    }

    /**
     * [2] 카카오 액세스 토큰 요청
     */
    public KakaoTokenResponseDto getKakaoAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) {
            params.add("client_secret", kakaoClientSecret);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> tokenRequestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponseDto> response =
                restTemplate.postForEntity(kakaoTokenUri, tokenRequestEntity, KakaoTokenResponseDto.class);

        return response.getBody();
    }

    /**
     * [3] 카카오 사용자 정보 조회
     */
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                kakaoUserInfoUri,
                HttpMethod.GET,
                request,
                Map.class
        );
        return response.getBody();
    }

    public void authenticateWithKakaoAndRedirect(String code, HttpServletResponse response) throws IOException {
        System.out.println("백엔드용 인가 코드: " + code);

        // 1. 카카오 액세스 토큰 받기
        KakaoTokenResponseDto tokenResponse = getKakaoAccessToken(code);

        // 2. 카카오 사용자 정보 요청
        Map<String, Object> kakaoUser = getKakaoUserInfo(tokenResponse.getAccessToken());
        if (kakaoUser == null || !kakaoUser.containsKey("kakao_account")) {
            response.sendRedirect("https://j12c107.p.ssafy.io/login?error=kakaoUserNotFound");
            return;
        }
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUser.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        if (email == null || email.isBlank()) {
            response.sendRedirect("https://j12c107.p.ssafy.io/login?error=emailNotFound");
            return;
        }

        Optional<LoginUserEntity> existingUserOpt = loginUserRepository.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            LoginUserEntity user = existingUserOpt.get();
            String accessTokenJwt = jwtUtil.generateAccessToken(user.getRole(), user.getEmail(), user.getNickname());
            String refreshTokenJwt = jwtUtil.generateRefreshToken(user.getRole(), user.getEmail(), user.getNickname());

            setRefreshTokenCookie(refreshTokenJwt, response);
            response.setHeader("Authorization", "Bearer " + accessTokenJwt);

            System.out.println("백엔드용 accesstoken 확인 : " + accessTokenJwt);

            response.sendRedirect("https://j12c107.p.ssafy.io/home/");
        } else {
            response.sendRedirect("https://j12c107.p.ssafy.io/user/signup?email=" + email);
        }
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);
    }

    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response, String email) {
        if (email == null) {
            return ResponseUtil.badRequest("인증된 사용자가 없습니다.", null);
        }

        removeJwtCookies(response);

        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout"
                + "?client_id=" + kakaoClientId
                + "&logout_redirect_uri=" + "https://j12c107.p.ssafy.io/api/auth/logout/callback";

        return ResponseUtil.success("로그아웃 완료", Map.of("redirectUri", kakaoLogoutUrl));
    }

    private void removeJwtCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public ResponseEntity<?> authenticateWithKakaoAndReturnJson(String code) {
        System.out.println("Received authorization code: " + code);

        KakaoTokenResponseDto tokenResponse = getKakaoAccessToken(code);
        Map<String, Object> kakaoUser = getKakaoUserInfo(tokenResponse.getAccessToken());
        if (kakaoUser == null || !kakaoUser.containsKey("kakao_account")) {
            return ResponseEntity.badRequest().body("카카오 사용자 정보 없음");
        }
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUser.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("이메일 정보 없음");
        }

        Optional<LoginUserEntity> existingUserOpt = loginUserRepository.findByEmail(email);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("신규 회원입니다. 가입 필요");
        }
        LoginUserEntity user = existingUserOpt.get();
        String accessTokenJwt = jwtUtil.generateAccessToken(user.getRole(), user.getEmail(), user.getNickname());
        String refreshTokenJwt = jwtUtil.generateRefreshToken(user.getRole(), user.getEmail(), user.getNickname());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshTokenJwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessTokenJwt);
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        System.out.println("JWT Access Token: " + accessTokenJwt);

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("accessToken", accessTokenJwt));
    }
}
