package com.c107.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final RestTemplate restTemplate;

    @Value("${finance.api.key}")
    private String financeApiKey;

    private static final String FINANCE_BASE_URL = "https://finopenapi.ssafy.io/ssafy/api/v1/member";

    /**
     * 금융 사용자의 userKey 반환 (존재 여부에 따라 검색 또는 생성)
     */
    public String getFinanceUserKey(String email) {
        // 요청 바디: apiKey와 userId(여기서는 email)
        Map<String, String> body = Map.of(
                "apiKey", financeApiKey,
                "userId", email
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. 계정 검색 호출 (search 엔드포인트)
        String searchUrl = FINANCE_BASE_URL + "/search";
        try {
            ResponseEntity<Map> searchResponse = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            if (searchResponse.getStatusCode() == HttpStatus.OK && searchResponse.getBody() != null) {
                String userKey = (String) searchResponse.getBody().get("userKey");
                if (userKey != null && !userKey.isBlank()) {
                    return userKey;
                }
            }
        } catch (Exception e) {
            // 검색 실패 시, 존재하지 않는 것으로 간주하고 생성 시도
            System.out.println("금융 사용자 검색 실패 또는 미존재: " + e.getMessage());
        }

        // 2. 계정 생성 호출 (계정이 존재하지 않는 경우)
        String createUrl = FINANCE_BASE_URL; // create 엔드포인트
        try {
            ResponseEntity<Map> createResponse = restTemplate.exchange(
                    createUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            // 200 OK 또는 201 CREATED를 성공으로 판단
            if ((createResponse.getStatusCode() == HttpStatus.OK || createResponse.getStatusCode() == HttpStatus.CREATED)
                    && createResponse.getBody() != null) {
                Object userKeyObj = createResponse.getBody().get("userKey");
                if (userKeyObj != null && userKeyObj instanceof String) {
                    return (String) userKeyObj;
                }
            }
            System.out.println("생성 API 응답 미확인: " + createResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("금융 사용자 생성 실패: " + e.getMessage());
        }

        return null;
    }
}
