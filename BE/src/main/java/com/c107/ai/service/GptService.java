package com.c107.ai.service;

import com.c107.ai.util.GptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptService {

    private final GptUtil gptUtil;

    /**
     * GPT에 요약 프롬프트를 전달하고, 응답(메모)을 받아 반환합니다.
     *
     * @param prompt 사용자(서비스) 레벨에서 만든 프롬프트 문자열
     * @return GPT가 생성한 메모 내용
     */
    public String generateMemo(String prompt) {
        log.info("GPT에 메모 생성 요청: {}", prompt);
        String response = gptUtil.askChatGPT(prompt);
        log.info("GPT 응답: {}", response);
        return response;
    }
}
