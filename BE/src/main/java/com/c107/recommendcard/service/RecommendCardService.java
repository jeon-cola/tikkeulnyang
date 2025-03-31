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

        // 각 상위 카테고리에 대해 budget_credit_mapping을 조회하여, 해당 카테고리와 매핑된 creditBenefitId 집합 구성
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

        // 각 상위 카테고리별로 추천 카드 조회
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
            // fallback
            List<RecommendCard> fallbackCards = recommendCardRepository.findByCreditBenefitCategory(category);
            recommendedCards.addAll(fallbackCards);
        }

        // 각 카드에 대해, 상위 카테고리 중 몇 개와 매핑되어 있는지 점수를 계산
        Map<RecommendCard, Long> cardScoreMap = recommendedCards.stream().collect(Collectors.toMap(
                card -> card,
                card -> {
                    // 해당 카드의 모든 혜택 조회
                    List<CreditCardBenefit> benefits = creditCardBenefitRepository.findByRecoCardId(card.getRecoCardId());
                    // 각 topCategory에 대해, 이 카드의 혜택이 해당 카테고리 매핑 집합에 속하는지 체크
                    long score = topCategories.stream().filter(category -> {
                        Set<Integer> mappedIds = categoryToCreditBenefitIds.getOrDefault(category, Collections.emptySet());
                        return benefits.stream().anyMatch(b -> mappedIds.contains(b.getCreditBenefitsId()));
                    }).count();
                    return score;
                }
        ));

        // 점수가 높은 순으로 내림차순 정렬 후, 동일하면 recoCardId 오름차순 정렬하여 limit 적용
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
                    List<CreditCardBenefit> benefits = creditCardBenefitRepository.findByRecoCardId(card.getRecoCardId());
                    String benefitDesc = benefits.stream()
                            .map(b -> b.getDescription())
                            .collect(Collectors.joining(", "));
                    // 예를 들어, 추가로 점수를 포함하고 싶다면 benefitDesc에 e.getValue()도 붙일 수 있습니다.
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
        // budget_category 데이터를 (categoryName -> budgetCategoryId) 맵으로 구성
        Map<String, Integer> budgetCategoryNameToId = budgetCategoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(b -> b.getCategoryName(), b -> b.getBudgetCategoryId()));

        // 각 상위 카테고리별로 BudgetCheckMapping을 조회해서 체크 혜택 ID 집합 구성
        Map<String, Set<Integer>> categoryToCheckBenefitIds = new HashMap<>();
        for (String category : topCategories) {
            Integer budgetCategoryId = budgetCategoryNameToId.get(category);
            if (budgetCategoryId != null) {
                List<Integer> checkBenefitIds = budgetCheckMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                        .stream()
                        .map(mapping -> mapping.getBenefitId())  // 여기서 올바른 필드(getBenefitId()) 사용
                        .collect(Collectors.toList());
                categoryToCheckBenefitIds.put(category, new HashSet<>(checkBenefitIds));
            }
        }

        // 각 상위 카테고리별로 추천 카드 조회
        for (String category : topCategories) {
            Integer budgetCategoryId = budgetCategoryNameToId.get(category);
            if (budgetCategoryId != null) {
                List<Integer> checkBenefitIds = budgetCheckMappingRepository.findByBudgetCategoryId(budgetCategoryId)
                        .stream()
                        .map(mapping -> mapping.getBenefitId())
                        .collect(Collectors.toList());
                if (!checkBenefitIds.isEmpty()) {
                    // 체크카드 전용 매핑 기반 조회 메서드 호출
                    List<RecommendCard> checkCards = recommendCardRepository.findByCheckBenefitIds(checkBenefitIds);
                    recommendedCards.addAll(checkCards);
                    continue;
                }
            }
            // 매핑 데이터가 없으면 fallback: 기존 방식으로 조회
            List<RecommendCard> fallbackCards = recommendCardRepository.findByCheckBenefitCategory(category);
            recommendedCards.addAll(fallbackCards);
        }

        // 각 카드에 대해 상위 카테고리와 매핑된 횟수를 점수로 계산
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

        // 점수가 높은 순 내림차순, 동일 시 recoCardId 오름차순 정렬 후 상위 10개 추천
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
                        return RecommendCardDetailResponseDto.CategoryBenefitDto.builder()
                                .category(benefit.getCategory())
                                .description(b.getDescription())
                                .build();
                    })
                    .collect(Collectors.toList());

        } else {
            List<CreditCardBenefit> benefits = creditCardBenefitRepository.findBySourceCardId(card.getSourceCardId());

            benefitDtos = benefits.stream()
                    .map(b -> {
                        CreditBenefit benefit = creditBenefitRepository.findById(b.getCreditBenefitsId())
                                .orElseThrow(() -> new RuntimeException("혜택 정보를 찾을 수 없습니다: " + b.getCreditBenefitsId()));
                        return RecommendCardDetailResponseDto.CategoryBenefitDto.builder()
                                .category(benefit.getCategory())
                                .description(b.getDescription())
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
