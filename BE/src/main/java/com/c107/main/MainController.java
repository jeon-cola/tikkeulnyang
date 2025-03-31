package com.c107.main;

import com.c107.budget.dto.BudgetRemainResponseDto;
import com.c107.common.util.ResponseUtil;
import com.c107.subscribe.dto.SubscribeResponseDto;
import com.c107.subscribe.service.SubscribeService;
import com.c107.bucket.dto.BucketListResponseDto;
import com.c107.bucket.service.BucketService;
import com.c107.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final BudgetService budgetService;
    private final SubscribeService subscribeService;
    private final BucketService bucketService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMainData(@AuthenticationPrincipal String email) {
        log.info("메인 정보 조회 요청: email={}", email);

        // 1. 예산 관련 정보 (남은 예산, 총 예산, 현재 소비 금액)
        BudgetRemainResponseDto budgetRemain = budgetService.getBudgetRemain(email);
        Integer remainingAmount = budgetRemain.getTotals().getTotalRemainingAmount();

        Integer totalBudget = budgetService.getTotalBudget(email);
        Integer currentConsumptionAmount = budgetService.getCurrentConsumption(email);

        double remainingPercentage = 0;
        if (totalBudget != null && totalBudget != 0) {
            remainingPercentage = (double) remainingAmount / totalBudget * 100;
        }

        // 2. 결제 예정 구독: 남은 일수가 가장 적은 구독 1건 선택
        List<SubscribeResponseDto> allUpcomingSubscriptions = subscribeService.getSubscribesByPaymentDateOrder(email);
        SubscribeResponseDto upcomingSubscription = null;
        if (allUpcomingSubscriptions != null && !allUpcomingSubscriptions.isEmpty()) {
            upcomingSubscription = allUpcomingSubscriptions.stream()
                    .min(Comparator.comparing(SubscribeResponseDto::getDaysRemaining))
                    .orElse(null);
        }

        // 3. 버킷리스트: 생성일(created_at)이 가장 최근인 항목 1건 선택
        List<BucketListResponseDto> bucketLists = bucketService.getBucketLists(email);
        BucketListResponseDto recentBucket = null;
        if (bucketLists != null && !bucketLists.isEmpty()) {
            recentBucket = bucketLists.stream()
                    .max(Comparator.comparing(BucketListResponseDto::getCreated_at))
                    .orElse(null);
        }

        // 4. 응답 데이터 구성
        Map<String, Object> mainData = new HashMap<>();
        Map<String, Object> remainingBudgetMap = new HashMap<>();
        remainingBudgetMap.put("amount", remainingAmount);
        remainingBudgetMap.put("percentage", remainingPercentage);

        mainData.put("remaining_budget", remainingBudgetMap);
        // 키 이름은 단수로 변경 (한 건만 전달)
        mainData.put("upcoming_subscription", upcomingSubscription);
        mainData.put("current_consumption_amount", currentConsumptionAmount);
        mainData.put("bucket", recentBucket);

        return ResponseUtil.success("메인 정보 조회 성공", mainData);
    }
}
