package com.c107.recommendcard.service;

import com.c107.recommendcard.dto.RecommendCardDetailResponseDto;
import com.c107.recommendcard.dto.RecommendCardResponseDto;
import com.c107.recommendcard.entity.*;
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
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
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
    private final CheckBenefitRepository checkBenefitRepository;
    private final CreditBenefitRepository creditBenefitRepository;


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
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 신용카드 추천
     */
    @Cacheable(value = "recommendCreditCardsCache", key = "#email", unless = "#result == null")
    @Transactional
    public List<RecommendCardResponseDto> recommendCreditCards(String email) {
        long start = System.currentTimeMillis(); // 응답 시간 측정 시작
        MDC.put("service", "recommendCreditCards");
        MDC.put("cache", "on"); // 캐싱 꺼두고 비교 시 off 로 수동 변경
        MDC.put("userEmail", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

            List<String> topCategories = extractTopCategories(user);
            log.info("신용카드 추천 대상 상위 카테고리: {}", topCategories);

            Set<RecommendCard> recommendedCards = new HashSet<>();
            Map<String, Integer> budgetCategoryNameToId = budgetCategoryRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(b -> b.getCategoryName(), b -> b.getBudgetCategoryId()));

            Map<String, Set<Integer>> categoryToCreditBenefitIds = new HashMap<>();
            for (String category : topCategories) {
                Integer budgetCategoryId = budgetCategoryNameToId.get(category);
                if (budgetCategoryId != null) {
                    List<Integer> benefitIds = budgetCheckMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                            .stream()
                            .map(mapping -> mapping.getBenefitId())
                            .collect(Collectors.toList());
                    categoryToCreditBenefitIds.put(category, new HashSet<>(benefitIds));
                }
            }

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

            Map<RecommendCard, Long> cardScoreMap = recommendedCards.stream().collect(Collectors.toMap(
                    card -> card,
                    card -> {
                        List<CreditCardBenefit> benefits = creditCardBenefitRepository.findByRecoCardId(card.getRecoCardId());
                        long score = topCategories.stream().filter(category -> {
                            Set<Integer> mappedIds = categoryToCreditBenefitIds.getOrDefault(category, Collections.emptySet());
                            return benefits.stream().anyMatch(b -> mappedIds.contains(b.getCreditBenefitsId()));
                        }).count();
                        return score;
                    }
            ));

            List<RecommendCardResponseDto> result = cardScoreMap.entrySet().stream()
                    .sorted((e1, e2) -> {
                        int cmp = Long.compare(e2.getValue(), e1.getValue());
                        if (cmp == 0) {
                            return Long.compare(e1.getKey().getRecoCardId(), e2.getKey().getRecoCardId());
                        }
                        return cmp;
                    })
                    .limit(10)
                    .map(e -> {
                        RecommendCard card = e.getKey();
                        List<CreditCardBenefit> benefits = creditCardBenefitRepository.findByRecoCardId(card.getRecoCardId());
                        String benefitDesc = benefits.stream()
                                .map(b -> b.getDescription())
                                .collect(Collectors.joining(", "));
                        return RecommendCardResponseDto.of(card, benefitDesc);
                    })
                    .collect(Collectors.toList());

            return result;
        } finally {
            long duration = System.currentTimeMillis() - start;
            MDC.put("durationMs", String.valueOf(duration));
            log.info("신용카드 추천 응답 완료"); // Kibana 시각화용 로그
            MDC.clear(); // 꼭 비워줘야 누적 안 됨
        }
    }

    /**
     * 체크카드 추천
     */
    @Cacheable(value = "recommendCheckCardsCache", key = "#email", unless = "#result == null")
    @Transactional
    public List<RecommendCardResponseDto> recommendCheckCards(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        List<String> topCategories = extractTopCategories(user);
        log.info("체크카드 추천 대상 상위 카테고리: {}", topCategories);

        Set<RecommendCard> recommendedCards = new HashSet<>();
        Map<String, Integer> budgetCategoryNameToId = budgetCategoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(b -> b.getCategoryName(), b -> b.getBudgetCategoryId()));

        Map<String, Set<Integer>> categoryToCheckBenefitIds = new HashMap<>();
        for (String category : topCategories) {
            Integer budgetCategoryId = budgetCategoryNameToId.get(category);
            if (budgetCategoryId != null) {
                List<Integer> checkBenefitIds = budgetCheckMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                        .stream()
                        .map(mapping -> mapping.getBenefitId())
                        .collect(Collectors.toList());
                categoryToCheckBenefitIds.put(category, new HashSet<>(checkBenefitIds));
            }
        }

        for (String category : topCategories) {
            Integer budgetCategoryId = budgetCategoryNameToId.get(category);
            if (budgetCategoryId != null) {
                List<Integer> checkBenefitIds = budgetCheckMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                        .stream()
                        .map(mapping -> mapping.getBenefitId())
                        .collect(Collectors.toList());
                if (!checkBenefitIds.isEmpty()) {
                    List<RecommendCard> checkCards = recommendCardRepository.findByCheckBenefitIds(checkBenefitIds);
                    recommendedCards.addAll(checkCards);
                    continue;
                }
            }
            List<RecommendCard> fallbackCards = recommendCardRepository.findByCheckBenefitCategory(category);
            recommendedCards.addAll(fallbackCards);
        }

        Map<RecommendCard, Long> cardScoreMap = recommendedCards.stream().collect(Collectors.toMap(
                card -> card,
                card -> {
                    List<CheckCardBenefit> benefits = checkCardBenefitRepository.findBySourceCardId(card.getSourceCardId());
                    long score = topCategories.stream().filter(category -> {
                        Set<Integer> mappedIds = categoryToCheckBenefitIds.getOrDefault(category, Collections.emptySet());
                        return benefits.stream().anyMatch(b -> mappedIds.contains(b.getBenefitId()));
                    }).count();
                    return score;
                }
        ));

        return cardScoreMap.entrySet().stream()
                .sorted((e1, e2) -> {
                    int cmp = Long.compare(e2.getValue(), e1.getValue());
                    if (cmp == 0) {
                        return Long.compare(e1.getKey().getRecoCardId(), e2.getKey().getRecoCardId());
                    }
                    return cmp;
                })
                .limit(10)
                .map(e -> {
                    RecommendCard card = e.getKey();
                    List<CheckCardBenefit> benefits = checkCardBenefitRepository.findBySourceCardId(card.getSourceCardId());
                    String benefitDesc = benefits.stream()
                            .map(b -> b.getDescription())
                            .collect(Collectors.joining(", "));
                    return RecommendCardResponseDto.of(card, benefitDesc);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public RecommendCardDetailResponseDto getCardDetail(int recoCardId) {
        RecommendCard card = recommendCardRepository.findById(recoCardId)
                .orElseThrow(() -> new RuntimeException("해당 카드가 없습니다: " + recoCardId));

        List<RecommendCardDetailResponseDto.CategoryBenefitDto> benefitDtos;

        if ("체크카드".equals(card.getCardType())) {
            List<CheckCardBenefit> benefits = checkCardBenefitRepository.findBySourceCardId(card.getSourceCardId());
            benefitDtos = benefits.stream()
                    .map(b -> {
                        CheckBenefit benefit = checkBenefitRepository.findById(b.getBenefitId())
                                .orElseThrow(() -> new RuntimeException("혜택 정보를 찾을 수 없습니다: " + b.getBenefitId()));

                        // BudgetCheckMapping을 통해 예산 카테고리 정보 조회
                        Optional<BudgetCheckMapping> mappingOpt = budgetCheckMappingRepository.findByBenefitId(b.getBenefitId());
                        String budgetCategoryName = "";
                        if (mappingOpt.isPresent()) {
                            int budgetCategoryId = mappingOpt.get().getBudgetCategoryId();
                            budgetCategoryName = budgetCategoryRepository.findById(budgetCategoryId)
                                    .map(bc -> bc.getCategoryName())
                                    .orElse("");
                        }

                        return RecommendCardDetailResponseDto.CategoryBenefitDto.builder()
                                .category(benefit.getCategory())
                                .description(b.getDescription())
                                .budgetCategory(budgetCategoryName)
                                .build();
                    })
                    .collect(Collectors.toList());
        } else {
            // 신용카드인 경우: BudgetCreditMapping 테이블을 사용하여 예산 카테고리 정보 조회
            List<CreditCardBenefit> benefits = creditCardBenefitRepository.findBySourceCardId(card.getSourceCardId());
            benefitDtos = benefits.stream()
                    .map(b -> {
                        CreditBenefit creditBenefit = creditBenefitRepository.findById(b.getCreditBenefitsId())
                                .orElseThrow(() -> new RuntimeException("혜택 정보를 찾을 수 없습니다: " + b.getCreditBenefitsId()));

                        // BudgetCreditMapping을 조회하여 예산 카테고리 정보 가져오기
                        Optional<BudgetCreditMapping> mappingOpt = budgetCreditMappingRepository.findByCreditBenefitsId(b.getCreditBenefitsId());
                        String budgetCategoryName = "";
                        if (mappingOpt.isPresent()) {
                            int budgetCategoryId = mappingOpt.get().getBudgetCategoryId();
                            budgetCategoryName = budgetCategoryRepository.findById(budgetCategoryId)
                                    .map(bc -> bc.getCategoryName())
                                    .orElse("");
                        }

                        return RecommendCardDetailResponseDto.CategoryBenefitDto.builder()
                                .category(creditBenefit.getCategory())
                                .description(b.getDescription())
                                .budgetCategory(budgetCategoryName)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return RecommendCardDetailResponseDto.builder()
                .recoCardId(Math.toIntExact(card.getRecoCardId()))
                .cardName(card.getRecoCardName())
                .cardType(card.getCardType())
                .corpName(card.getCorpName())
                .imagePath(card.getImagePath())
                .benefits(benefitDtos)
                .build();
    }
}
