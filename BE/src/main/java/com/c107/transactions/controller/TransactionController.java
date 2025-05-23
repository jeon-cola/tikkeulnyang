package com.c107.transactions.controller;

import com.c107.common.exception.CustomException;
import com.c107.common.util.ResponseUtil;
import com.c107.transactions.dto.TransactionCreateRequest;
import com.c107.transactions.dto.TransactionUpdateRequest;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(
            @AuthenticationPrincipal String email,
            @RequestBody TransactionCreateRequest request) {

        Transaction createdTransaction = transactionService.createTransaction(email, request);
        return ResponseUtil.success("거래 내역이 성공적으로 생성되었습니다.", createdTransaction);
    }


    /**
     * 거래 수정 엔드포인트
     *
     * HTTP Method: PUT
     * URL: /api/transactions/{transactionId}
     *
     * Request Body: TransactionUpdateRequest (수정할 필드들)
     * Response: 수정된 거래 내역
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionUpdateRequest request) {
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, request);
        return ResponseEntity.ok(updatedTransaction);
    }

    /**
     * 거래 삭제 엔드포인트 (소프트 딜리트)
     *
     * HTTP Method: DELETE
     * URL: /api/transactions/{transactionId}
     *
     * Response: 성공 메시지
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok("거래 내역이 삭제 되었습니다.");
    }
}
