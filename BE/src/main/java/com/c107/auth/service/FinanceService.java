package com.c107.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final RestTemplate restTemplate;

    @Value("${finance.api.key}")
    private String financeApiKey;

    private static final String FINANCE_BASE_URL = "https://finopenapi.ssafy.io/ssafy/api/v1/member";

    /**
     * 금융 사용자의 userKey 반환 (검색 → 없으면 생성)
     */
    @Cacheable(value = "financeUserKeyCache", key = "#email")
    public String getFinanceUserKey(String email) {
        // (1) 검색
        String userKey = doSearch(email);
        if (userKey != null) {
            return userKey;
        }
        // (2) 검색 실패 → 생성
        userKey = doCreate(email);
        return userKey; // null 가능
    }

    /**
     * [검색 로직] POST /search
     * Postman에서 확인한 JSON Body와 동일하게:
     * {
     *   "apiKey": "...",
     *   "userId": "..."
     * }
     */
    private String doSearch(String email) {
        String searchUrl = FINANCE_BASE_URL + "/search";

        // Body 구성 (Postman과 동일한 필드명/대소문자)
        Map<String, String> requestBody = Map.of(
                "apiKey", financeApiKey,
                "userId", email
        );

        // Header 구성 (Postman과 동일)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // Accept: application/json

        // 요청 엔티티
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 로그로 확인
        System.out.println("\n--- [doSearch] ---");
        System.out.println("URL: " + searchUrl);
        System.out.println("Request Body: " + requestBody);
        System.out.println("Request Headers: " + headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    searchUrl, HttpMethod.POST, requestEntity, Map.class
            );

            // 응답 로깅
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String userKey = (String) response.getBody().get("userKey");
                if (userKey != null && !userKey.isBlank()) {
                    return userKey;
                }
            }
        } catch (HttpClientErrorException e) {
            // 4xx
            String body = e.getResponseBodyAsString();
            System.out.println("Search HttpClientErrorException: " + e.getStatusCode() + " / " + body);

            // "이미 존재하는 ID"가 있다면 → 재검색
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST && body.contains("이미 존재하는 ID")) {
                System.out.println("검색에서 '이미 존재하는 ID' => doSearchAgain()");
                return doSearchAgain(email);
            }
        } catch (Exception e) {
            System.out.println("Search Exception: " + e.getMessage());
        }
        return null;
    }

    /**
     * [생성 로직] POST /member
     * Body 동일: { "apiKey": "...", "userId": "..." }
     */
    private String doCreate(String email) {
        String createUrl = FINANCE_BASE_URL;

        Map<String, String> requestBody = Map.of(
                "apiKey", financeApiKey,
                "userId", email
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 로그
        System.out.println("\n--- [doCreate] ---");
        System.out.println("URL: " + createUrl);
        System.out.println("Request Body: " + requestBody);
        System.out.println("Request Headers: " + headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    createUrl, HttpMethod.POST, requestEntity, Map.class
            );

            // 응답 로깅
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if ((response.getStatusCode() == HttpStatus.OK
                    || response.getStatusCode() == HttpStatus.CREATED)
                    && response.getBody() != null) {

                String userKey = (String) response.getBody().get("userKey");
                if (userKey != null && !userKey.isBlank()) {
                    return userKey;
                }
            }
            System.out.println("금융 사용자 생성 실패, 응답코드: "
                    + (response != null ? response.getStatusCode() : "null"));
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            System.out.println("Create HttpClientErrorException: " + e.getStatusCode() + " / " + body);

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST && body.contains("이미 존재하는 ID")) {
                System.out.println("생성 중 '이미 존재하는 ID' => doSearchAgain()");
                return doSearchAgain(email);
            }
        } catch (Exception e) {
            System.out.println("Create Exception: " + e.getMessage());
        }
        return null;
    }

    /**
     * [재검색 로직] - "이미 존재하는 ID" 시
     */
    private String doSearchAgain(String email) {
        System.out.println("\n--- [doSearchAgain] ---");
        String searchUrl = FINANCE_BASE_URL + "/search";

        Map<String, String> requestBody = Map.of(
                "apiKey", financeApiKey,
                "userId", email
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 로그
        System.out.println("URL: " + searchUrl);
        System.out.println("Request Body: " + requestBody);
        System.out.println("Request Headers: " + headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    searchUrl, HttpMethod.POST, requestEntity, Map.class
            );
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                String userKey = (String) response.getBody().get("userKey");
                if (userKey != null && !userKey.isBlank()) {
                    return userKey;
                }
            }
        } catch (Exception e) {
            System.out.println("doSearchAgain Exception: " + e.getMessage());
        }
        return null;
    }
}
