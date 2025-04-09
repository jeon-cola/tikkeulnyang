package com.c107.budget.controller;

import com.c107.budget.dto.BudgetRemainResponseDto;
import com.c107.budget.dto.BudgetRequestDto;
import com.c107.budget.dto.BudgetResponseDto;
import com.c107.budget.dto.CategoryResponseDto;
import com.c107.budget.entity.BudgetEntity;
import com.c107.budget.service.BudgetService;
import com.c107.common.util.ResponseUtil;
import com.c107.subscribe.entity.SubscribeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping("/plan")
    public ResponseEntity<?> getBudgetPlan(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        BudgetResponseDto responseDto = budgetService.getBudgetPlan(email, targetYear, targetMonth);
        return ResponseUtil.success("예산 계획 조회에 성공했습니다.", responseDto);
    }

    @PostMapping("/plan")
    public ResponseEntity<?> createBudgetPlan(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestBody BudgetRequestDto requestDto
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        BudgetResponseDto responseDto = budgetService.createBudget(email, requestDto, targetYear, targetMonth);
        return ResponseUtil.success("예산 계획이 성공적으로 등록되었습니다.", responseDto);
    }
    @GetMapping("/remain")
    public ResponseEntity<?> getBudgetRemain(@AuthenticationPrincipal String email) {
        BudgetRemainResponseDto responseDto = budgetService.getBudgetRemain(email);
        return ResponseUtil.success("남은 예산 조회에 성공했습니다.", responseDto);
    }


    @GetMapping("/waste/money")
    public ResponseEntity<?> getWasteMoney(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        // 캐시 초기화 (문제 해결 후 제거 가능)
        budgetService.clearWasteMoneyCache();

        BudgetResponseDto.BudgetWaste responseDto = budgetService.getWasteMoney(
                email, targetYear, targetMonth);

        return ResponseUtil.success("낭비 금액 조회에 성공했습니다.", responseDto);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        CategoryResponseDto responseDto = budgetService.getAllCategories(email, targetYear, targetMonth);
        return ResponseUtil.success("카테고리 목록 조회에 성공했습니다.", responseDto);
    }
}
