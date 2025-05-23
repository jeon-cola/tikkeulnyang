package com.c107.ai.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.c107.ai.config.GptConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GptUtil {

    private final GptConfig gptConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askChatGPT(String userPrompt) {
        String apiUrl = gptConfig.getApiUrl();
        String apiKey = gptConfig.getApiKey();

        if (apiKey == null || apiKey.isBlank()) {
            log.error("❌ GPT API 키가 설정되지 않음!");
            return "GPT API 오류: API 키가 설정되지 않았습니다.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userPrompt));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("❌ GPT API 요청 실패", e);
            return "GPT API 오류: " + e.getMessage();
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String content = root.path("choices").get(0).path("message").path("content").asText();
                return content;
            } catch (Exception e) {
                log.error("❌ GPT 응답 파싱 오류", e);
                return "GPT 응답 오류: " + e.getMessage();
            }
        } else {
            log.error("❌ GPT API 실패: HTTP Status {}", response.getStatusCode());
            return "GPT API 오류: 응답 실패 (" + response.getStatusCode() + ")";
        }
    }

}
