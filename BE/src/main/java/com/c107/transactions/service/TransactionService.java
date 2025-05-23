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
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_MONITOR");

    @Transactional
    public Transaction createTransaction(String email, TransactionCreateRequest request) {
        long start = System.currentTimeMillis();
        try {
            MDC.put("event_type", "transaction_create");
            MDC.put("userEmail", email);
            MDC.put("transactionAmount", String.valueOf(request.getAmount()));
            MDC.put("merchantName", request.getMerchantName());
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

            Map<String, Object> logDetails = new HashMap<>();
            logDetails.put("event_type", "transaction_create");
            logDetails.put("status", "SUCCESS");
            logDetails.put("userId", user.getUserId());
            logDetails.put("email", email);
            logDetails.put("amount", request.getAmount());
            logDetails.put("responseTimeMs", elapsed);
            logDetails.put("merchantName", request.getMerchantName());
            logDetails.put("transactionType", request.getTransactionType());
            logDetails.put("logger_name", "SECURITY_MONITOR");

            securityLogger.info("Transaction Created: {}", logDetails);
            return saved;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;

            // 실패 시 보안 로깅
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("event_type", "transaction_create_failure");
            errorDetails.put("status", "FAIL");
            errorDetails.put("email", email);
            errorDetails.put("amount", request.getAmount());
            errorDetails.put("responseTimeMs", elapsed);
            errorDetails.put("errorMessage", e.getMessage());
            errorDetails.put("logger_name", "SECURITY_MONITOR");

            securityLogger.error("Transaction Creation Failed: {}", errorDetails);

            throw e;
        } finally {
            MDC.clear();
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
        long start = System.currentTimeMillis();

        try {
            MDC.put("event_type", "transaction_update");
            MDC.put("transactionId", String.valueOf(transactionId));

            // transaction_id가 int 타입이므로 변환 (만약 repository의 ID 타입이 Integer라면)
            Transaction transaction = transactionRepository.findById(Math.toIntExact(transactionId))
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "거래 내역을 찾을 수 없습니다."));

            transaction.setAmount(request.getAmount());
            transaction.setTransactionDate(request.getTransactionDate());
            transaction.setCategoryId(request.getCategoryId());
            transaction.setMerchantName(request.getMerchantName());

            transaction.setUpdatedAt(LocalDateTime.now());
            Transaction updatedTransaction = transactionRepository.save(transaction);

            long elapsed = System.currentTimeMillis() - start;

            // JSON 보안 로깅
            Map<String, Object> updateDetails = new HashMap<>();
            updateDetails.put("event_type", "transaction_update");
            updateDetails.put("transactionId", transactionId);
            updateDetails.put("amount", request.getAmount());
            updateDetails.put("merchantName", request.getMerchantName());
            updateDetails.put("responseTimeMs", elapsed);
            updateDetails.put("logger_name", "SECURITY_MONITOR");

            securityLogger.info("Transaction Updated: {}", updateDetails);

            return updatedTransaction;
        } catch (Exception e) {
            // 에러 로깅
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("event_type", "transaction_update_failure");
            errorDetails.put("transactionId", transactionId);
            errorDetails.put("errorMessage", e.getMessage());
            errorDetails.put("logger_name", "SECURITY_MONITOR");

            securityLogger.error("Transaction Update Failed: {}", errorDetails);

            throw e;
        } finally {
            MDC.clear();
        }
    }

    /**
     * 거래 삭제 기능 (소프트 딜리트)
     * @param transactionId 삭제할 거래의 ID
     */
    @Transactional
    public void deleteTransaction(Long transactionId) {
        long start = System.currentTimeMillis();
        try {
            MDC.put("event_type", "transaction_delete");
            MDC.put("transactionId", String.valueOf(transactionId));

            Transaction transaction = transactionRepository.findById(Math.toIntExact(transactionId))
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "거래 내역을 찾을 수 없습니다."));

            // deleted 컬럼을 1로 업데이트 (소프트 딜리트)
            transaction.setDeleted(1);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            long elapsed = System.currentTimeMillis() - start;
            Map<String, Object> deleteDetails = new HashMap<>();
            deleteDetails.put("event_type", "transaction_delete");
            deleteDetails.put("transactionId", transactionId);
            deleteDetails.put("responseTimeMs", elapsed);
            deleteDetails.put("logger_name", "SECURITY_MONITOR");

            securityLogger.info("Transaction Deleted: {}", deleteDetails);

        } catch (Exception e) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("event_type", "transaction_delete_failure");
            errorDetails.put("transactionId", transactionId);
            errorDetails.put("errorMessage", e.getMessage());
            errorDetails.put("logger_name", "SECURITY_MONITOR");

            securityLogger.error("Transaction Deletion Failed: {}", errorDetails);

            throw e;
        } finally {
            MDC.clear();
        }
    }
}
