package com.c107.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Kakao의 응답 데이터를 가져옵니다.
        OAuth2User user = super.loadUser(userRequest);

        // 필요한 경우, user.getAttributes()를 활용해 추가 사용자 정보를 추출 및 가공할 수 있습니다.
        // 예: Map<String, Object> attributes = user.getAttributes();

        return user;
    }
}
