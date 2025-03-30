package com.c107.recommendcard.service;

import com.c107.recommendcard.dto.RecommendCardResponseDto;
import com.c107.recommendcard.entity.CheckCardBenefit;
import com.c107.recommendcard.entity.CreditCardBenefit;
import com.c107.recommendcard.entity.RecommendCard;
import com.c107.recommendcard.repository.*;
import com.c107.paymenthistory.entity.CategoryEntity;
import com.c107.paymenthistory.repository.BudgetCategoryRepository;
import com.c107.paymenthistory.repository.CategoryRepository;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendCardService {

    private final RecommendCardRepository recommendCardRepository;
    private final BudgetCreditMappingRepository budgetCreditMappingRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CreditCardBenefitRepository creditCardBenefitRepository;
    private final BudgetCheckMappingRepository budgetCheckMappingRepository;
    private final CheckCardBenefitRepository checkCardBenefitRepository;

    /**
     * 소비 내역에서 상위 소비 카테고리를 추출하는 공통 로직
     */
    private List<String> extractTopCategories(User user) {
        List<Transaction> transactions = transactionRepository.findByUserId(user.getUserId());
        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Integer> categoryAmounts = new HashMap<>();
        // budget_category 테이블 데이터를 Map으로 (key: budgetCategoryId, value: categoryName)
        Map<Integer, String> budgetCategoryMap = budgetCategoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(b -> b.getBudgetCategoryId(), b -> b.getCategoryName()));
        // 인버트한 맵 (key: categoryName, value: budgetCategoryId)
        Map<String, Integer> budgetCategoryNameToId = budgetCategoryMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == null || transaction.getTransactionType() == 1) {
                continue;
            }
            int amount = transaction.getAmount();
            String budgetCategoryName = "기타";

            if (transaction.getCategoryId() != null) {
                Optional<CategoryEntity> optCat = categoryRepository.findById(transaction.getCategoryId());
                if (optCat.isPresent() && optCat.get().getBudgetCategory() != null) {
                    String mappedName = budgetCategoryMap.get(optCat.get().getBudgetCategory());
                    if (mappedName != null) {
                        budgetCategoryName = mappedName;
                    }
                }
            } else if (transaction.getMerchantName() != null) {
                List<CategoryEntity> matchingCategories = categoryRepository.findByMerchantName(transaction.getMerchantName());
                if (!matchingCategories.isEmpty() && matchingCategories.get(0).getBudgetCategory() != null) {
                    String mappedName = budgetCategoryMap.get(matchingCategories.get(0).getBudgetCategory());
                    if (mappedName != null) {
                        budgetCategoryName = mappedName;
                    }
                }
            }
            categoryAmounts.put(budgetCategoryName,
                    categoryAmounts.getOrDefault(budgetCategoryName, 0) + amount);
        }
        return categoryAmounts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 신용카드 추천
     */
    @Transactional
    public List<RecommendCardResponseDto> recommendCreditCards(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        List<String> topCategories = extractTopCategories(user);
        log.info("신용카드 추천 대상 상위 카테고리: {}", topCategories);

        Set<RecommendCard> recommendedCards = new HashSet<>();
        Map<String, Integer> budgetCategoryNameToId = budgetCategoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(b -> b.getCategoryName(), b -> b.getBudgetCategoryId()));

        for (String category : topCategories) {
            Integer budgetCategoryId = budgetCategoryNameToId.get(category);
            if (budgetCategoryId != null) {
                List<Integer> creditBenefitIds = budgetCreditMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                        .stream()
                        .map(mapping -> mapping.getCreditBenefitsId())
                        .collect(Collectors.toList());
                if (!creditBenefitIds.isEmpty()) {
                    List<RecommendCard> creditCards = recommendCardRepository.findByCreditBenefitIds(creditBenefitIds);
                    recommendedCards.addAll(creditCards);
                    continue;
                }
            }
            List<RecommendCard> fallbackCards = recommendCardRepository.findByCreditBenefitCategory(category);
            recommendedCards.addAll(fallbackCards);
        }
        // 정렬: recoCardId 오름차순, limit 5 적용
        return recommendedCards.stream()
                .sorted(Comparator.comparing(RecommendCard::getRecoCardId))
                .limit(5)
                .map(card -> {
                    // 해당 카드의 혜택 내용 조회
                    List<CreditCardBenefit> benefits = creditCardBenefitRepository.findByRecoCardId(card.getRecoCardId());
                    // 혜택 내용과 해당 카드의 혜택 카테고리를 문자열로 결합 (예: "카테고리명: 혜택내용")
                    String benefitDesc = benefits.stream()
                            .map(b -> b.getDescription())
                            .collect(Collectors.joining(", "));
                    return RecommendCardResponseDto.of(card, benefitDesc);
                })
                .collect(Collectors.toList());
    }

    /**
     * 체크카드 추천
     */
    @Transactional
    public List<RecommendCardResponseDto> recommendCheckCards(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        List<String> topCategories = extractTopCategories(user);
        log.info("체크카드 추천 대상 상위 카테고리: {}", topCategories);

        Set<RecommendCard> recommendedCards = new HashSet<>();
        // budget_category 테이블 데이터를 Map (categoryName -> budgetCategoryId)
        Map<String, Integer> budgetCategoryNameToId = budgetCategoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(b -> b.getCategoryName(), b -> b.getBudgetCategoryId()));

        for (String category : topCategories) {
            Integer budgetCategoryId = budgetCategoryNameToId.get(category);
            if (budgetCategoryId != null) {
                // BudgetCheckMapping을 이용해 check_benefits_id 목록 조회
                List<Integer> checkBenefitIds = budgetCheckMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                        .stream()
                        .map(mapping -> mapping.getCheckBenefitsId())
                        .collect(Collectors.toList());
                if (!checkBenefitIds.isEmpty()) {
                    // 기존의 findByBenefitCategory 메서드 대신, checkBenefitIds를 조건으로 하는 쿼리를 추가할 수 있다면 사용합니다.
                    // 만약 해당 Repository 메서드가 없다면, 기존 방식으로 조회하고, 필요에 따라 필터링 할 수 있습니다.
                    // 예를 들어:
                    List<RecommendCard> checkCards = recommendCardRepository.findByBenefitCategory(category);
                    recommendedCards.addAll(checkCards);
                    continue;
                }
            }
            List<RecommendCard> fallbackCards = recommendCardRepository.findByBenefitCategory(category);
            recommendedCards.addAll(fallbackCards);
        }
        // 정렬: recoCardId 오름차순, limit 5 적용
        return recommendedCards.stream()
                .sorted(Comparator.comparing(RecommendCard::getRecoCardId))
                .limit(5)
                .map(card -> {
                    // 해당 카드의 체크카드 혜택 내용 조회
                    List<CheckCardBenefit> benefits = checkCardBenefitRepository.findByRecoCardId(card.getRecoCardId());
                    String benefitDesc = benefits.stream()
                            .map(b -> b.getDescription())
                            .collect(Collectors.joining(", "));
                    return RecommendCardResponseDto.of(card, benefitDesc);
                })
                .collect(Collectors.toList());
    }

}
