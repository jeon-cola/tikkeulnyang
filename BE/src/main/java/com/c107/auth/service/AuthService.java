package com.c107.auth.service;

import com.c107.auth.dto.KakaoTokenResponseDto;
import com.c107.auth.dto.JwtTokenResponseDto;
import com.c107.auth.entity.LoginUserEntity;
import com.c107.auth.repository.LoginUserRepository;
import com.c107.common.util.JwtUtil;
import com.c107.common.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginUserRepository loginuserRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    // user-info-uri는 동의 항목이 없으면 거의 빈값만 내려옴.
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    /**
     * 1) 인가 코드(code)로 카카오 액세스 토큰 요청
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
     * 2) (선택) 카카오 사용자 정보 호출
     * 동의 항목이 없으면 거의 { id: ~ } 정도만 내려옴.
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

    /**
     * 3) JWT 발급 및 회원가입 유무 확인
     * - 만약 DB에 사용자가 없다면 회원가입이 필요하다고 응답 (신규 회원)
     * - 이미 존재하면 JWT를 발급하여 바로 로그인 처리
     */
    public ResponseEntity<Map<String, Object>> authenticateWithKakao(String code) {
        // (1) 액세스 토큰 받기
        KakaoTokenResponseDto tokenResponse = getKakaoAccessToken(code);

        // (2) 사용자 정보 요청 (동의 항목이 없으면 거의 id만 내려옴)
        Map<String, Object> kakaoUser = getKakaoUserInfo(tokenResponse.getAccessToken());
        // 임시로 id만 가져오기
        String kakaoId = kakaoUser.getOrDefault("id", "kakao-unknown").toString();

        // (3) 신규 회원인 경우 DB에 회원이 없으므로 추가 회원가입 페이지로 이동 필요.
        // 예: 이메일, 닉네임 등 추가 정보를 프론트엔드에서 입력 받음.
        // 여기서는 기본 email, nickname을 생성하지 않고 "SIGNUP_REQUIRED"로 응답.
        String defaultEmail = "unknown-" + kakaoId + "@noemail.com";
        Optional<LoginUserEntity> existingUser = loginuserRepository.findByEmail(defaultEmail);
        if (existingUser.isEmpty()) {
            // 신규 회원: 추가 정보 입력이 필요하다는 응답을 반환
            return ResponseUtil.success("SIGNUP_REQUIRED", Map.of("kakaoId", kakaoId));
        }

        // (4) 기존 회원인 경우, JWT 생성
        LoginUserEntity user = existingUser.get();
        String accessTokenJwt = jwtUtil.generateAccessToken(user.getRole(), user.getEmail(), user.getNickname());
        String refreshTokenJwt = jwtUtil.generateRefreshToken(user.getRole(), user.getEmail(),user.getNickname());

        return ResponseUtil.success("JWT 발급 성공", new JwtTokenResponseDto(accessTokenJwt, refreshTokenJwt));
    }
}
