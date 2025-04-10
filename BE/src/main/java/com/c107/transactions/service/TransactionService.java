package com.c107.transactions.service;

import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.paymenthistory.entity.BudgetCategoryEntity;
import com.c107.paymenthistory.entity.CategoryEntity;
import com.c107.paymenthistory.repository.BudgetCategoryRepository;
import com.c107.paymenthistory.repository.CardRepository;
import com.c107.paymenthistory.repository.CategoryRepository;
import com.c107.transactions.dto.TransactionCreateRequest;
import com.c107.transactions.dto.TransactionUpdateRequest;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Transaction createTransaction(String email, TransactionCreateRequest request) {
        long start = System.currentTimeMillis();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

            // 카테고리 검증
            Integer categoryId = request.getCategoryId();
            if (categoryId != null) {
                budgetCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 예산 카테고리입니다."));
            }

            // 카테고리 ID가 null이면 기본 카테고리 사용
            if (categoryId == null) {
                Optional<BudgetCategoryEntity> defaultCategory = budgetCategoryRepository.findByCategoryName("기타");
                categoryId = defaultCategory.map(BudgetCategoryEntity::getBudgetCategoryId).orElse(null);
            }

            // 날짜 생성
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime transactionDate = LocalDateTime.of(
                    request.getYear(),
                    request.getMonth(),
                    request.getDay(),
                    now.getHour(),
                    now.getMinute(),
                    now.getSecond()
            );

            // 새 거래 내역 생성
            Transaction newTransaction = new Transaction();
            newTransaction.setUserId(user.getUserId());
            newTransaction.setCardId(0); // 기타 카드로 처리
            newTransaction.setTransactionType(request.getTransactionType());
            newTransaction.setAmount(request.getAmount());
            newTransaction.setTransactionDate(transactionDate);
            newTransaction.setCategoryId(categoryId);
            newTransaction.setMerchantName(request.getMerchantName());
            newTransaction.setIsWaste(0);
            newTransaction.setDeleted(0);
            newTransaction.setCreatedAt(now);
            newTransaction.setUpdatedAt(now);

            Transaction saved = transactionRepository.save(newTransaction);

            long elapsed = System.currentTimeMillis() - start;
            logger.info("TRANSACTION_LOG | type=CREATE | status=SUCCESS | userId={} | amount={} | responseTimeMs={}",
                    user.getUserId(), request.getAmount(), elapsed);
            return saved;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            logger.error("TRANSACTION_LOG | type=CREATE | status=FAIL | email={} | amount={} | responseTimeMs={}",
                    email, request.getAmount(), elapsed, e);
            throw e;
        }
    }

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
        long start = System.currentTimeMillis();
        try {
            Transaction transaction = transactionRepository.findById(Math.toIntExact(transactionId))
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "거래 내역을 찾을 수 없습니다."));

            // deleted 컬럼을 1로 업데이트 (소프트 딜리트)
            transaction.setDeleted(1);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            long elapsed = System.currentTimeMillis() - start;
            logger.info("TRANSACTION_LOG | type=DELETE | status=SUCCESS | transactionId={} | responseTimeMs={}",
                    transactionId, elapsed);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            logger.error("TRANSACTION_LOG | type=DELETE | status=FAIL | transactionId={} | responseTimeMs={}",
                    transactionId, elapsed, e);
            throw e;
        }
    }
}
