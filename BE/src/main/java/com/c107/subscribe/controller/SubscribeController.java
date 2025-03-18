package com.c107.subscribe.controller;

import com.c107.common.util.ResponseUtil;
import com.c107.subscribe.dto.SubscribeListResponseDto;
import com.c107.subscribe.dto.SubscribeRequestDto;
import com.c107.subscribe.dto.SubscribeResponseDto;
import com.c107.subscribe.entity.SubscribeEntity;
import com.c107.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
@Slf4j
public class SubscribeController {

    private final SubscribeService subscribeService;

    // 구독 정보 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerSubscribe(
            @AuthenticationPrincipal String email,  // JWT에서 가져온 이메일
            @RequestBody @Validated SubscribeRequestDto requestDto) {
        log.info("구독 정보 등록 요청: email={}", email);

        SubscribeEntity savedSubscribe = subscribeService.registerSubscribe(email, requestDto);
        return ResponseUtil.success("구독 정보가 성공적으로 등록되었습니다.",
                Map.of("subscribeId", savedSubscribe.getSubscribeId()));
    }

    // 사용자의 모든 구독 정보 조회
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserSubscribes(@AuthenticationPrincipal String email) {
        log.info("사용자 구독 목록 조회 요청: email={}", email);

        List<SubscribeEntity> subscribes = subscribeService.getUserSubscribes(email);
        return ResponseUtil.success("구독 목록 조회 성공", subscribes);
    }

    // 결제일 순으로 구독 리스트 조회
    @GetMapping("/day")
    public ResponseEntity<Map<String, Object>> getSubscribesByPaymentDate(@AuthenticationPrincipal String email) {
        log.info("구독 리스트 결제일 순 조회 요청");
        List<SubscribeResponseDto> sortedSubscribes = subscribeService.getSubscribesByPaymentDateOrder(email);
        SubscribeListResponseDto response = SubscribeListResponseDto.forPaymentDateOrder(sortedSubscribes);
        return ResponseUtil.success("결제일 순으로 구독 리스트 조회 성공", response);
    }

    // 금액 순으로 구독 리스트 조회
    @GetMapping("/expensive")
    public ResponseEntity<Map<String, Object>> getSubscribesByPrice(@AuthenticationPrincipal String email) {
        log.info("구독 리스트 금액 순 조회 요청");
        List<SubscribeResponseDto> sortedSubscribes = subscribeService.getSubscribesByPriceOrder(email);
        SubscribeListResponseDto response = SubscribeListResponseDto.forPriceOrder(sortedSubscribes);
        return ResponseUtil.success("금액 순으로 구독 리스트 조회 성공", response);
    }

    // 구독 정보를 삭제
    @DeleteMapping("/{subscribeId}")
    public ResponseEntity<Map<String, Object>> deleteSubscribe(
            @AuthenticationPrincipal String email,
            @PathVariable Integer subscribeId) {
        log.info("구독 삭제 요청: email={}, subscribeId={}", email, subscribeId);

        subscribeService.deleteSubscribe(email, subscribeId);
        return ResponseUtil.success("구독 정보가 성공적으로 삭제되었습니다.", null);
    }

}
