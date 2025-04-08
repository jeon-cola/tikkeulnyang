package com.c107.ai.service;

import com.c107.ai.util.GptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptService {

    private final GptUtil gptUtil;

    public String analyzeWorkout(String userText) {
        if (userText == null || userText.trim().isEmpty()) {
            log.warn("⚠️ GPT 프롬프트가 비어 있음! 기본값을 사용합니다.");
            userText = "운동 기록을 입력하세요.";
        }

        String prompt = """
                당신은 헬스장 회사의 AI 운동 분석 도우미입니다.
                당신의 임무는 STT로 변환된 문장의 **철자 오류, 단위 오류, 숫자 오류, 불일치**를 수정한 후,
                올바른 운동 기록을 JSON 형식으로 변환하는 것입니다.
        """.formatted(userText);

        log.info("🔍 GPT prompt = {}", prompt);
        return gptUtil.askChatGPT(prompt);

    }
    
}
