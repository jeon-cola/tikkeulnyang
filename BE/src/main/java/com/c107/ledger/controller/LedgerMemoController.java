// com.c107.ledger.controller.LedgerMemoController.java
package com.c107.ledger.controller;

import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.common.util.ResponseUtil;
import com.c107.ledger.dto.GenerateMemoRequest;
import com.c107.ledger.entity.LedgerMemo;
import com.c107.ledger.service.LedgerMemoService;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import com.c107.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class LedgerMemoController {

    private final LedgerMemoService memoService;
    private final UserRepository userRepository;

    /**
     * {
     *   "date": "2025-04-09"
     * }
     */
    @PostMapping
    public ResponseEntity<?> generateMemo(@RequestBody GenerateMemoRequest request) {
        // 1) 유효성 검증 (원한다면)
        if (request.getDate() == null) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "date 필드는 필수입니다.");
        }

        // 2) 로그인 유저 조회
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));


        // 3) 메모 생성
        LedgerMemo memo = memoService.generateMemoForDate(request.getDate(), user.getUserId());

        // 4) 결과 응답
        if (memo == null) {
            // 거래 내역이 없으면 빈 응답
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(memo);
    }
}
