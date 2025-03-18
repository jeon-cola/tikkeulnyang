package com.c107.budget.controller;

import com.c107.budget.dto.BudgetRequestDto;
import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.service.BudgetService;
import com.c107.common.util.ResponseUtil;
import com.c107.subscribe.entity.SubscribeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping("/plan")
    public ResponseEntity<?> createBudgetPlan(
            @AuthenticationPrincipal String email,
            @RequestBody BudgetRequestDto requestDto) {

        BudgetResponseDto responseDto = budgetService.createBudget(email, requestDto);
        return ResponseUtil.success("예산 계획이 성공적으로 등록되었습니다.", responseDto);


//        try {
//            BudgetResponseDto responseDto = budgetService.createBudget(token, requestDto);
//            return ResponseEntity.ok(responseDto);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("예산 설정 중 오류 발생: " + e.getMessage());
//        }
    }
}
