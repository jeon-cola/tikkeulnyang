package com.c107.accounts.controller;

import com.c107.accounts.entity.Account;
import com.c107.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountSyncService;

    /**
     * 프론트엔드의 "계좌 최신화" 버튼을 누르면 호출되는 엔드포인트.
     * Open API를 통해 계좌 목록을 조회한 후, DB를 최신 상태로 동기화합니다.
     *
     * @param userKey             각 유저의 회원가입 시 저장된 USER KEY (ex: 유저테이블의 값)
     * @param accountTypeUniqueNo 계좌 유형 고유번호 (예: "001-1-8eac6d1f7cfe48")
     * @return 동기화 후 DB에 저장된 모든 계좌 목록
     */
    @PostMapping("/refresh")
    public ResponseEntity<List<Account>> refreshAccounts(
            @RequestParam String userKey,
            @RequestParam String accountTypeUniqueNo) {
        List<Account> accounts = accountSyncService.refreshAccounts(userKey, accountTypeUniqueNo);
        return ResponseEntity.ok(accounts);
    }
}
