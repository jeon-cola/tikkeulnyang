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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
@Slf4j
public class
SubscribeController {

    private final SubscribeService subscribeService;

    // 구독 정보 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerSubscribe(@RequestBody @Validated SubscribeRequestDto requestDto) {
        SubscribeEntity savedSubscribe = subscribeService.registerSubscribe(requestDto);
        return ResponseUtil.success("구독 정보가 성공적으로 등록되었습니다.",
                Map.of("subscribeId", savedSubscribe.getSubscribeId()));
    }

    // 사용자의 모든 구독 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserSubscribes(@PathVariable Integer userId) {
        log.info("사용자 구독 목록 조회 요청: userId={}", userId);
        List<SubscribeEntity> subscribes = subscribeService.getUserSubscribes(userId);
        return ResponseUtil.success("구독 목록 조회 성공", subscribes);
    }

    // 결제일 순으로 구독 리스트 조회
    @GetMapping("/day/{userId}")
    public ResponseEntity<Map<String, Object>> getSubscribesByPaymentDate() {
        log.info("구독 리스트 결제일 순 조회 요청");
        List<SubscribeResponseDto> sortedSubscribes = subscribeService.getSubscribesByPaymentDateOrder();
        SubscribeListResponseDto response = SubscribeListResponseDto.forPaymentDateOrder(sortedSubscribes);
        return ResponseUtil.success("결제일 순으로 구독 리스트 조회 성공", response);
    }

    // 금액 순으로 구독 리스트 조회
    @GetMapping("/expensive/{userId}")
    public ResponseEntity<Map<String, Object>> getSubscribesByPrice() {
        log.info("구독 리스트 금액 순 조회 요청");
        List<SubscribeResponseDto> sortedSubscribes = subscribeService.getSubscribesByPriceOrder();
        SubscribeListResponseDto response = SubscribeListResponseDto.forPriceOrder(sortedSubscribes);
        return ResponseUtil.success("금액 순으로 구독 리스트 조회 성공", response);
    }

    // 구독 정보를 삭제
    @DeleteMapping("/{subscribeId}")
    public ResponseEntity<Map<String, Object>> deleteSubscribe(@PathVariable Integer subscribeId) {
        log.info("구독 삭제 요청: subscribeId={}", subscribeId);
        subscribeService.deleteSubscribe(subscribeId);
        return ResponseUtil.success("구독 정보가 성공적으로 삭제되었습니다.", null);
    }
}
