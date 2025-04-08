package com.c107.auth.service;

import com.c107.auth.dto.KakaoTokenResponseDto;
import com.c107.common.util.JwtUtil;
import com.c107.common.util.ResponseUtil;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository loginUserRepository;
    private final JwtUtil jwtUtil;
    private final FinanceService financeService;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_MONITOR");
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    // base URL를 properties 파일에서 주입받음
    @Value("${app.base.url}")
    private String baseUrl;

    // 로그인 시도 추적을 위한 메모리 기반 저장소
    private static final Map<String, List<LoginAttempt>> loginAttempts = new ConcurrentHashMap<>();

    // 로그인 시도 정보를 저장하는 내부 클래스
    private static class LoginAttempt {
        LocalDateTime timestamp;

        LoginAttempt(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    public void redirectToKakaoLogin(HttpServletResponse response) throws IOException {
        String loginUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&response_type=code";
        response.sendRedirect(loginUrl);
    }

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
        try {
            // MDC 컨텍스트 설정
            MDC.put("event_type", "login_attempt");
            MDC.put("auth_method", "kakao_oauth");

            // 로그인 시도 로깅
            securityLogger.info(createSecurityLogMessage("login_attempt", Map.of(
                    "auth_method", "kakao_oauth"
            )));

            KakaoTokenResponseDto tokenResponse = getKakaoAccessToken(code);
            Map<String, Object> kakaoUser = getKakaoUserInfo(tokenResponse.getAccessToken());

            // 비정상 접근 탐지
            if (kakaoUser == null || !kakaoUser.containsKey("kakao_account")) {
                securityLogger.warn(createSecurityLogMessage("login_anomaly", Map.of(
                        "reason", "incomplete_user_info"
                )));

                response.sendRedirect(baseUrl + "/login?error=kakaoUserNotFound");
                return;
            }

            Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUser.get("kakao_account");
            String email = (String) kakaoAccount.get("email");

            // 이메일 없음 감지
            if (email == null || email.isBlank()) {
                securityLogger.warn(createSecurityLogMessage("login_risk", Map.of(
                        "reason", "missing_email",
                        "oauth_provider", "kakao"
                )));

                response.sendRedirect(baseUrl + "/login?error=emailNotFound");
                return;
            }

            // 로그인 시도 기록
            recordLoginAttempt(email);

            // 다중 로그인 시도 감지
            if (checkMultipleLoginAttempts(email)) {
                securityLogger.error(createSecurityLogMessage("login_flood", Map.of(
                        "email", email,
                        "risk_level", "high"
                )));

                // 로그인 차단 로직
                response.sendRedirect(baseUrl + "/login?error=tooManyAttempts");
                return;
            }

            Optional<User> existingUserOpt = loginUserRepository.findByEmail(email);

            if (existingUserOpt.isPresent()) {
                User user = existingUserOpt.get();

                // 로그인 성공 로깅
                securityLogger.info(createSecurityLogMessage("login_success", Map.of(
                        "email", email,
                        "user_role", user.getRole()
                )));

                // 기존 로그인 로직
                String accessTokenJwt = jwtUtil.generateAccessToken(user.getRole(), user.getEmail(), user.getNickname());
                String refreshTokenJwt = jwtUtil.generateRefreshToken(user.getRole(), user.getEmail(), user.getNickname());

                setAccessTokenCookie(accessTokenJwt, response);
                setRefreshTokenCookie(refreshTokenJwt, response);

                response.sendRedirect(baseUrl + "/home/");
            } else {
                // 신규 사용자 감지 로깅
                securityLogger.info(createSecurityLogMessage("new_user_detected", Map.of(
                        "email", email,
                        "oauth_provider", "kakao"
                )));

                response.sendRedirect(baseUrl + "/user/signup?email=" + email);
            }
        } catch (Exception e) {
            // 예외 상황 보안 로깅
            securityLogger.error(createSecurityLogMessage("login_error", Map.of(
                    "error_type", e.getClass().getSimpleName(),
                    "error_message", e.getMessage()
            )), e);

            response.sendRedirect(baseUrl + "/login?error=systemError");
        } finally {
            MDC.clear();
        }
    }
    // 다중 로그인 시도 확인 메서드 (1시간 내 10회 이상)
    private boolean checkMultipleLoginAttempts(String email) {
        List<LoginAttempt> attempts = loginAttempts.getOrDefault(email, new ArrayList<>());

        // 1시간 내 로그인 시도 횟수 계산
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentAttempts = attempts.stream()
                .filter(attempt -> attempt.timestamp.isAfter(oneHourAgo))
                .count();

        // 1시간 내 10회 이상 로그인 시도 시 위험으로 판단
        return recentAttempts > 10;
    }

    // 로그인 시도 기록 메서드
    private void recordLoginAttempt(String email) {
        List<LoginAttempt> attempts = loginAttempts.computeIfAbsent(
                email, k -> new ArrayList<>()
        );

        // 최근 시도 추가
        attempts.add(new LoginAttempt(LocalDateTime.now()));

        // 24시간 이상된 시도 제거
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        attempts.removeIf(attempt -> attempt.timestamp.isBefore(dayAgo));
    }

    // 보안 로그 메시지 생성 유틸리티 메서드
    private String createSecurityLogMessage(String eventType, Map<String, Object> details) {
        try {
            Map<String, Object> logMessage = new HashMap<>();
            logMessage.put("timestamp", LocalDateTime.now());
            logMessage.put("event_type", eventType);
            logMessage.putAll(details);
            return objectMapper.writeValueAsString(logMessage);
        } catch (Exception e) {
            return "Security log creation failed: " + eventType;
        }
    }

    private void setAccessTokenCookie(String accessToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)(accessTokenExpiration() / 1000));
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    private long accessTokenExpiration() {
        return 3600000L;
    }

    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response, String email) {
        if (email == null) {
            return ResponseUtil.badRequest("인증된 사용자가 없습니다.", null);
        }

        removeJwtCookies(response);

        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout"
                + "?client_id=" + kakaoClientId
                + "&logout_redirect_uri=" + baseUrl + "/logout/callback";

        return ResponseUtil.success("로그아웃 완료", Map.of("redirectUri", kakaoLogoutUrl));
    }

    private void removeJwtCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);  // Secure 플래그 추가

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);  // Secure 플래그 추가

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
//

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

        Optional<User> existingUserOpt = loginUserRepository.findByEmail(email);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("신규 회원입니다. 가입 필요");
        }
        User user = existingUserOpt.get();
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
