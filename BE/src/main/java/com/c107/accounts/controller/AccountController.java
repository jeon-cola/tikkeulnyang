package com.c107.accounts.controller;

import com.c107.accounts.entity.Account;
import com.c107.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Open API를 통해 생성된 계좌 정보를 로컬 DB에 저장하는 API.
     *
     * @param userKey             Open API 사용자 키
     * @param userId              로컬 사용자 ID
     * @param accountTypeUniqueNo 계좌 유형 고유번호
     * @return 저장된 Account 엔티티
     */
    @PostMapping("/sync")
    public ResponseEntity<Account> syncAccount(
            @RequestParam String userKey,
            @RequestParam Integer userId,
            @RequestParam String accountTypeUniqueNo) {
        Account account = accountService.syncAccountFromOpenAPI(userKey, userId, accountTypeUniqueNo);
        return ResponseEntity.ok(account);
    }
}
