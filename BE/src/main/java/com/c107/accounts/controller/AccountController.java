package com.c107.accounts.controller;

import com.c107.accounts.dto.DepositChargeRequest;
import com.c107.accounts.service.AccountService;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.common.util.ResponseUtil;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    // 계좌 동기화 엔드포인트 (전체 계좌 등록/업데이트)
    @GetMapping("/refresh")
    public ResponseEntity<List<?>> refreshAccounts(Authentication authentication) {
        String email = getEmailFromAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        List<?> updatedAccounts = accountService.refreshAccounts(userId);
        return ResponseEntity.ok(updatedAccounts);
    }

    // 예치금 충전
    @PostMapping("/deposit-charge")
    public ResponseEntity<String> depositCharge(Authentication authentication,
                                                @RequestBody DepositChargeRequest depositChargeRequest) {
        String email = getEmailFromAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        accountService.depositCharge(userId, depositChargeRequest.getAmount());
        return ResponseEntity.ok("예치금 충전 요청이 완료되었습니다.");
    }

    // 예치금 환불
    @PostMapping("/refund-deposit")
    public ResponseEntity<String> refundDeposit(Authentication authentication,
                                                @RequestBody DepositChargeRequest depositChargeRequest) {
        String email = getEmailFromAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        accountService.refundDeposit(userId, depositChargeRequest.getAmount());
        return ResponseEntity.ok("예치금 환불 요청이 완료되었습니다.");
    }

    // 대표계좌 설정
    @PostMapping("/set-representative")
    public ResponseEntity<String> setRepresentative(Authentication authentication,
                                                    @RequestParam String accountNo) {
        String email = getEmailFromAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        accountService.setRepresentativeAccount(user.getUserId(), accountNo);
        return ResponseEntity.ok("대표계좌가 설정되었습니다.");
    }

    // 가계부 거래내역 가져오기: 기존 transactions 테이블의 마지막 거래일 이후의 거래내역을 가져옴
    @PostMapping("/sync-transactions")
    public ResponseEntity<String> syncTransactions(Authentication authentication,
                                                   @RequestParam Integer defaultCategoryId) {
        String email = getEmailFromAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        accountService.syncNewTransactions(user.getUserId(), defaultCategoryId);
        return ResponseEntity.ok("신규 거래내역 동기화 요청이 완료되었습니다.");
    }

    // 공통 로직: 인증 객체에서 이메일 추출
    private String getEmailFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return (String) authentication.getPrincipal();
    }

    // 계좌 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAccountList(Authentication authentication) {
        String email = getEmailFromAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        List<Map<String, Object>> accountList = accountService.getAccountList(userId);
        return ResponseUtil.success("계좌 목록 조회 성공", Map.of("accounts", accountList));
    }
}
