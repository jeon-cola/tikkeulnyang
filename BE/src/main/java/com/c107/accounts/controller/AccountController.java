package com.c107.accounts.controller;

import com.c107.accounts.dto.DepositChargeRequest;
import com.c107.accounts.entity.Account;
import com.c107.accounts.service.AccountService;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository; // UserRepository 주입

    @GetMapping("/refresh")
    public ResponseEntity<List<Account>> refreshAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        // userDetails.getUsername()은 이메일을 반환하므로, 해당 이메일로 사용자 조회
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        List<Account> updatedAccounts = accountService.refreshAccounts(userId);
        return ResponseEntity.ok(updatedAccounts);
    }

    // 예치금 충전 엔드포인트 (서비스 계좌로 이체)
    @PostMapping("/deposit-charge")
    public ResponseEntity<String> depositCharge(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody DepositChargeRequest depositChargeRequest) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        // AccountService의 depositCharge 메서드 호출
        accountService.depositCharge(userId,
                depositChargeRequest.getSelectedAccountNo(),
                depositChargeRequest.getAmount());

        return ResponseEntity.ok("예치금 충전 요청이 완료되었습니다.");
    }
}


