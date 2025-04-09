// com.c107.ledger.service.LedgerMemoService.java
package com.c107.ledger.service;

import com.c107.ledger.entity.LedgerMemo;
import com.c107.ledger.repository.LedgerMemoRepository;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import com.c107.ai.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerMemoService {

    private final TransactionRepository transactionRepository;
    private final LedgerMemoRepository memoRepository;
    private final GptService gptService;

    @Transactional
    public LedgerMemo generateMemoForDate(LocalDate date, Integer userId) {
        // 1. Skip if already exists
        if (memoRepository.findByMemoDate(date).isPresent()) {
            return memoRepository.findByMemoDate(date).get();
        }

        // 2. Fetch transactions for that date & user
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();
        List<Transaction> txns = transactionRepository
                .findAllByUserIdAndTransactionDateBetween(userId, start, end);

        if (txns.isEmpty()) {
            return null; // or save a “no activity” memo
        }

        // 3. Build a short summary
        String summary = buildSummaryPrompt(txns);

        // 4. Ask GPT
        String memoContent = gptService.generateMemo(summary);

        // 5. Persist
        LedgerMemo memo = LedgerMemo.builder()
                .memoDate(date)
                .content(memoContent)
                .createdAt(LocalDateTime.now())
                .build();
        return memoRepository.save(memo);
    }

    private String buildSummaryPrompt(List<Transaction> txns) {
        StringBuilder sb = new StringBuilder();

        sb.append("다음은 사용자의 오늘 소비 내역입니다.\n");
        sb.append("각 거래에는 가맹점 이름과 금액이 포함되어 있습니다.\n");
        sb.append("거래 내역을 분석하여 소비 습관을 유쾌한 고양이 말투로 요약해 주세요.\n");
        sb.append("카테고리도 알아서 추측해서 요약하고, 소비 습관에 대해 귀엽고 가벼운 조언도 포함해주세요.\n");
        sb.append("마지막은 '냥~' 또는 '다옹!' 같은 말투로 마무리해 주세요.\n\n");

        txns.stream()
                .filter(t -> t.getDeleted() == 0) // 🐾 삭제된 거래 제외
                .forEach(t -> sb.append(String.format("- %s: %d원\n", t.getMerchantName(), t.getAmount())));

        return sb.toString();
    }

}
