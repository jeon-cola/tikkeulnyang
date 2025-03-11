package com.c107.subscribe.controller;

import com.c107.subscribe.dto.SubscribeListResponseDto;
import com.c107.subscribe.dto.SubscribeRequestDto;
import com.c107.subscribe.dto.SubscribeResponseDto;
import com.c107.subscribe.entity.SubscribeEntity;
import com.c107.subscribe.service.SubscribeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
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
    public ResponseEntity<?> registerSubscribe(@RequestBody @Validated SubscribeRequestDto requestDto) {

        try {
            SubscribeEntity savedSubscribe = subscribeService.registerSubscribe(requestDto);

            Map<String, Object> response = new HashMap<>();
            response.put("subscribeId", savedSubscribe.getSubscribeId());
            response.put("message", "구독 정보가 성공적으로 등록되었습니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("구독 등록 중 오류 발생: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "구독 등록에 실패했습니다.");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //사용자의 모든 구독 정보 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSubscribes(@PathVariable Integer userId) {
        log.info("사용자 구독 목록 조회 요청: userId={}", userId);

        try {
            List<SubscribeEntity> subscribes = subscribeService.getUserSubscribes(userId);
            return ResponseEntity.ok(subscribes);
        } catch (Exception e) {
            log.error("사용자 구독 목록 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "구독 목록 조회에 실패했습니다.");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 결제일 순으로 구독 리스트 조회
    @GetMapping("/day/{userId}")
    public ResponseEntity<?> getSubscribesByPaymentDate() {
        log.info("구독 리스트 결제일 순 조회 요청");

        try {
            List<SubscribeResponseDto> sortedSubscribes = subscribeService.getSubscribesByPaymentDateOrder();
            SubscribeListResponseDto response = SubscribeListResponseDto.forPaymentDateOrder(sortedSubscribes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("구독 리스트 결제일 순 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "구독 리스트 결제일 순 조회에 실패했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 금액 순으로 구독 리스트 조회
    @GetMapping("/expensive/{userId}")
    public ResponseEntity<?> getSubscribesByPrice() {
        log.info("구독 리스트 금액 순 조회 요청");

        try {
            List<SubscribeResponseDto> sortedSubscribes = subscribeService.getSubscribesByPriceOrder();
            SubscribeListResponseDto response = SubscribeListResponseDto.forPriceOrder(sortedSubscribes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("구독 리스트 금액 순 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "구독 리스트 금액 순 조회에 실패했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    //구독 정보를 삭제
    @DeleteMapping("/{subscribeId}")
    public ResponseEntity<?> deleteSubscribe(@PathVariable Integer subscribeId) {
        log.info("구독 삭제 요청: subscribeId={}", subscribeId);

        try {
            subscribeService.deleteSubscribe(subscribeId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "구독 정보가 성공적으로 삭제되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("구독 삭제 중 오류 발생: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "구독 삭제에 실패했습니다.");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
