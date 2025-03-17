package com.c107.budget.controller;

import com.c107.budget.dto.BudgetRequestDto;
import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping("/plan")
    public ResponseEntity<?> createBudgetPlan(
            @RequestHeader("Authorization") String token,
            @RequestBody BudgetRequestDto requestDto
    ) {

        try {

            BudgetResponseDto responseDto = budgetService.createBudget(token, requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예산 설정 중 오류 발생: " + e.getMessage());
        }
    }
}
