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

    }

    @GetMapping("/plan")
    public ResponseEntity<?> getBudgetPlan(@AuthenticationPrincipal String email) {
        BudgetResponseDto responseDto = budgetService.getBudgetPlan(email);
        return ResponseUtil.success("예산 계획 조회에 성공했습니다.", responseDto);
    }
}
