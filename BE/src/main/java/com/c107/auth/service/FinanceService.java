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
     * 금융 사용자 조회
     */
    public String searchFinanceUser(String email) {
        String url = FINANCE_BASE_URL + "/search";
        Map<String, String> body = Map.of(
                "apiKey", financeApiKey,
                "userId", email
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("userKey");
            }
        } catch (Exception e) {
            System.out.println("금융 사용자 조회 실패: " + e.getMessage());
        }
        return null;
    }

    /**
     * 금융 사용자 생성 (회원가입)
     */
    public String createFinanceUser(String email, String nickname) {
        String url = FINANCE_BASE_URL;
        Map<String, String> body = Map.of(
                "apiKey", financeApiKey,
                "userId", email,
                "userName", nickname
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("userKey");
            }
        } catch (Exception e) {
            System.out.println("금융 사용자 생성 실패: " + e.getMessage());
        }
        return null;
    }
}
