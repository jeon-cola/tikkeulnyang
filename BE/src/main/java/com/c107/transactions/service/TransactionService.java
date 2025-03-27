package com.c107.transactions.service;

import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.transactions.dto.TransactionUpdateRequest;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    /**
     * 거래 수정 기능
     * @param transactionId 수정할 거래의 ID
     * @param request 수정할 내용이 담긴 DTO
     * @return 수정된 거래 엔티티
     */
    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionUpdateRequest request) {
        // transaction_id가 int 타입이므로 변환 (만약 repository의 ID 타입이 Integer라면)
        Transaction transaction = transactionRepository.findById(Math.toIntExact(transactionId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "거래 내역을 찾을 수 없습니다."));

        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCategoryId(request.getCategoryId());
        transaction.setMerchantName(request.getMerchantName());

        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction updatedTransaction = transactionRepository.save(transaction);
        logger.info("거래 내역 수정 완료: ID = {}", transactionId);
        return updatedTransaction;
    }

    /**
     * 거래 삭제 기능 (소프트 딜리트)
     * @param transactionId 삭제할 거래의 ID
     */
    @Transactional
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(Math.toIntExact(transactionId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "거래 내역을 찾을 수 없습니다."));

        // deleted 컬럼을 1로 업데이트 (소프트 딜리트)
        transaction.setDeleted(1);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        logger.info("거래 내역 소프트 딜리트 완료: ID = {}", transactionId);
    }
}
