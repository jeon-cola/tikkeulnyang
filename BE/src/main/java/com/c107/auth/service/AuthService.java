package com.c107.auth.service;

import com.c107.auth.dto.KakaoTokenResponseDto;
import com.c107.common.util.JwtUtil;
import com.c107.common.util.ResponseUtil;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    @Value("${app.base.url}")
    private String baseUrl;

    // 로그인 시도 추적을 위한 메모리 기반 저장소
    private static final Map<String, LoginAttemptTracker> loginAttempts = new ConcurrentHashMap<>();

    // 로그인 시도 추적 클래스
    private static class LoginAttemptTracker {
        List<LoginAttempt> attempts = new ArrayList<>();
        int failedAttempts = 0;
        LocalDateTime lastBlockedTime = null;

        void addAttempt(LoginAttempt attempt) {
            attempts.add(attempt);
            if (!attempt.isSuccess) {
                failedAttempts++;
            } else {
                failedAttempts = 0;
            }
        }
    }

    // 로그인 시도 상세 정보
    private static class LoginAttempt {
        LocalDateTime timestamp;
        String ipAddress;
        String userAgent;
        boolean isSuccess;
        String eventType;

        LoginAttempt(LocalDateTime timestamp, String ipAddress, String userAgent, boolean isSuccess, String eventType) {
            this.timestamp = timestamp;
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.isSuccess = isSuccess;
            this.eventType = eventType;
        }

        // 기존 호환성을 위한 생성자
        LoginAttempt(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            this.ipAddress = null;
            this.userAgent = null;
            this.isSuccess = false;
            this.eventType = null;
        }
    }

    // 보안 로그 헬퍼 메서드
    private void logSecurityEvent(String eventType, Map<String, Object> details, HttpServletRequest request) {
        try {
            Map<String, Object> logEntry = new HashMap<>();
            LocalDateTime currentTime = LocalDateTime.now();
            logEntry.put("timestamp", currentTime);
            logEntry.put("event_type", eventType);
            logEntry.put("logger_name", "SECURITY_MONITOR");

            // 클라이언트 정보 추가
            if (request != null) {
                // IP 주소 정보
                String ipAddress = getClientIpAddress(request);
                logEntry.put("ip", ipAddress);

                // User-Agent 정보 추가
                String userAgent = request.getHeader("User-Agent");
                logEntry.put("user_agent", userAgent);

                // 요청 경로 정보
                logEntry.put("request_uri", request.getRequestURI());

                // 참조 페이지 정보
                String referer = request.getHeader("Referer");
                if (referer != null) {
                    logEntry.put("referer", referer);
                }
            }

            // 기존 세부 정보 추가
            logEntry.putAll(details);

            String jsonLog = objectMapper.writeValueAsString(logEntry);
            securityLogger.info(jsonLog);

            // 로그인 시도 기록
            if (details.containsKey("email")) {
                recordLoginAttempt(eventType, logEntry);
            }
        } catch (Exception e) {
            securityLogger.error("보안 로그 생성 실패: " + eventType, e);
        }
    }

    // IP 주소 추출 메서드
    private String getClientIpAddress(HttpServletRequest request) {
        String[] ipHeaderCandidates = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_CLIENT_IP",
                "HTTP_CLIENT"
        };

        for (String header : ipHeaderCandidates) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    // 로그인 시도 기록 메서드
    private void recordLoginAttempt(String eventType, Map<String, Object> logEntry) {
        String email = (String) logEntry.get("email");
        if (email == null) return;

        String ipAddress = (String) logEntry.get("ip");
        String userAgent = (String) logEntry.get("user_agent");

        LoginAttempt attempt = new LoginAttempt(
                LocalDateTime.now(),
                ipAddress,
                userAgent,
                isSuccessfulEvent(eventType),
                eventType
        );

        LoginAttemptTracker tracker = loginAttempts.computeIfAbsent(email, k -> new LoginAttemptTracker());
        tracker.addAttempt(attempt);

        // 로그인 시도 및 비정상 접근 패턴 감지
        checkLoginAttemptPatterns(email, tracker);
    }

    // 로그인 이벤트 성공 여부 판단
    private boolean isSuccessfulEvent(String eventType) {
        return eventType.equals("login_success") || eventType.equals("auto_login");
    }

    // 로그인 시도 패턴 분석
    private void checkLoginAttemptPatterns(String email, LoginAttemptTracker tracker) {
        LocalDateTime now = LocalDateTime.now();

        // 1시간 내 과도한 로그인 시도 감지
        long recentAttempts = tracker.attempts.stream()
                .filter(attempt -> attempt.timestamp.isAfter(now.minusHours(1)))
                .count();

        // 다중 로그인 시도(login_flood) 탐지
        if (recentAttempts > 10) {
            Map<String, Object> floodDetails = new HashMap<>();
            floodDetails.put("email", email);
            floodDetails.put("attempts_count", recentAttempts);
            floodDetails.put("risk_level", "high");

            logSecurityEvent("login_flood", floodDetails, null);
        }

        // 연속된 로그인 실패 감지
        if (tracker.failedAttempts >= 5) {
            Map<String, Object> failureDetails = new HashMap<>();
            failureDetails.put("email", email);
            failureDetails.put("failed_attempts", tracker.failedAttempts);
            failureDetails.put("risk_level", "medium");

            logSecurityEvent("repeated_login_failure", failureDetails, null);
        }
    }

    // 기존 메서드들은 동일하게 유지...
    // (authenticateWithKakaoAndRedirect, logout 등)

    // 기존의 checkMultipleLoginAttempts 메서드
    private boolean checkMultipleLoginAttempts(String email) {
        LoginAttemptTracker tracker = loginAttempts.getOrDefault(email, new LoginAttemptTracker());
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentAttempts = tracker.attempts.stream()
                .filter(attempt -> attempt.timestamp.isAfter(oneHourAgo))
                .count();
        return recentAttempts > 10;
    }

    // 기존 코드와의 호환성을 위한 메서드
    private void recordLoginAttempt(String email) {
        LoginAttempt attempt = new LoginAttempt(LocalDateTime.now());

        LoginAttemptTracker tracker = loginAttempts.computeIfAbsent(email, k -> new LoginAttemptTracker());
        tracker.addAttempt(attempt);

        // 오래된 시도 제거 (1일)
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        tracker.attempts.removeIf(a -> a.timestamp.isBefore(dayAgo));
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
        accessTokenCookie.setSecure(true);
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
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
}
