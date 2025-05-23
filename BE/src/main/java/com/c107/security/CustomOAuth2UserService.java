package com.c107.security;

import com.c107.common.util.JwtUtil;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JwtUtil jwtUtil;

    public CustomOAuth2UserService(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User user = super.loadUser(userRequest);


        Map<String, Object> attributes = user.getAttributes();

        String email = null;
        String nickname = null;

        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        if (attributes.containsKey("properties")) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            nickname = (String) properties.get("nickname");
        }

        String role = "USER";

        String accessToken = jwtUtil.generateAccessToken(role, email, nickname);
        String refreshToken = jwtUtil.generateRefreshToken(role, email, nickname);

        System.out.println("Access Token 발급 완료 : " + accessToken);
        System.out.println("Refresh Token 발급 완료 : " + refreshToken);

        return user;
    }
}
