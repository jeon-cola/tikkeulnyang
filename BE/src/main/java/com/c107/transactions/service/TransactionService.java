package com.c107.transactions.service;

import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.paymenthistory.repository.BudgetCategoryRepository;
import com.c107.paymenthistory.repository.CardRepository;
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

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;

    @Transactional
    public Transaction createTransaction(String email, TransactionCreateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Integer userId = user.getUserId();
        Integer cardId = request.getCardId();

        // 카드가 사용자의 카드가 아니면 0으로 처리
        if (cardId != null) {
            boolean isUserCard = cardRepository.findById(cardId)
                    .map(card -> userId.equals(card.getUserId()))
                    .orElse(false);

            // 사용자의 카드가 아니면 0으로 설정
            if (!isUserCard) {
                cardId = 0;
                logger.info("유효하지 않은 카드, 기타 카드(0)로 변경: 사용자 = {}", userId);
            }
        }

        // 카드 ID가 null이면 0으로 설정
        if (cardId == null) {
            cardId = 0;
        }

        // 거래 유형 변환 (1: 수입, 2: 지출)
        Integer transactionType = request.getTransactionType();

        // 날짜 생성 (시간은 현재 시간 사용)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime transactionDate = LocalDateTime.of(
                request.getYear(),
                request.getMonth(),
                request.getDay(),
                now.getHour(),
                now.getMinute(),
                now.getSecond()
        );

        // 카테고리 ID 유효성 검증
        Integer categoryId = request.getCategoryId();
        if (categoryId != null) {
            // 해당 카테고리가 존재하는지 확인
            boolean categoryExists = budgetCategoryRepository.existsById(categoryId);
            if (!categoryExists) {
                throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 카테고리입니다.");
            }
        }

        // 새 거래 내역 생성
        Transaction newTransaction = new Transaction();
        newTransaction.setUserId(userId);
        newTransaction.setCardId(cardId);
        newTransaction.setTransactionType(transactionType);
        newTransaction.setAmount(request.getAmount());
        newTransaction.setTransactionDate(transactionDate);
        newTransaction.setCategoryId(categoryId);
        newTransaction.setMerchantName(request.getMerchantName());
        newTransaction.setIsWaste(0); // 기본값은 낭비 아님
        newTransaction.setDeleted(0); // 기본값은 미삭제
        newTransaction.setCreatedAt(now);
        newTransaction.setUpdatedAt(now);

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        logger.info("새 거래 내역 생성 완료: ID = {}, 사용자 = {}, 날짜 = {}-{}-{}",
                savedTransaction.getTransactionId(), userId,
                request.getYear(), request.getMonth(), request.getDay());

        return savedTransaction;
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
        Transaction transaction = transactionRepository.findById(Math.toIntExact(transactionId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "거래 내역을 찾을 수 없습니다."));

        // deleted 컬럼을 1로 업데이트 (소프트 딜리트)
        transaction.setDeleted(1);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        logger.info("거래 내역 소프트 딜리트 완료: ID = {}", transactionId);
    }
}
