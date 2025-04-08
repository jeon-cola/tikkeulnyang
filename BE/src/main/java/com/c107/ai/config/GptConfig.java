package com.c107.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GptConfig {

    @Value("${gpt.api.key}")
    private String apiKey;

    @Value("${gpt.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
