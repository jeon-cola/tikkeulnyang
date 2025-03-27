package com.c107.cards.service;

import com.c107.cards.dto.CardResponseDto;
import com.c107.cards.dto.CardTransactionResponseDto;
import com.c107.cards.entity.CardInfoEntity;
import com.c107.cards.repository.CardInfoRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.paymenthistory.entity.BudgetCategoryEntity;
import com.c107.paymenthistory.entity.CardEntity;
import com.c107.paymenthistory.entity.CategoryEntity;
import com.c107.paymenthistory.entity.PaymentHistoryEntity;
import com.c107.paymenthistory.repository.BudgetCategoryRepository;
import com.c107.paymenthistory.repository.CategoryRepository;
import com.c107.paymenthistory.repository.PaymentHistoryRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    @Value("${finance.api.key}")
    private String financeApiKey;
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Open API를 호출하여 사용자의 모든 카드 정보를 DB에 등록 또는 업데이트
     */
    @Transactional
    public CardResponseDto refreshCards(String email) {
        logger.info("카드 정보 동기화 시작: {}", LocalDateTime.now());

        // 로그인한 사용자의 정보를 DB에서 조회하여 financeUserKey 확보
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Integer userId = user.getUserId();
        String userKey = user.getFinanceUserKey();

        // Open API 호출 준비
        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/creditCard/inquireSignUpCreditCardList";
        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + String.format("%06d", new Random().nextInt(1000000));

        Map<String, Object> header = new HashMap<>();
        header.put("apiName", "inquireSignUpCreditCardList");
        header.put("transmissionDate", transmissionDate);
        header.put("transmissionTime", transmissionTime);
        header.put("institutionCode", "00100");
        header.put("fintechAppNo", "001");
        header.put("apiServiceCode", "inquireSignUpCreditCardList");
        header.put("institutionTransactionUniqueNo", institutionTransactionUniqueNo);
        header.put("apiKey", financeApiKey);
        header.put("userKey", userKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        // responseMap에 응답 본문 저장
        Map<String, Object> responseMap = responseEntity.getBody();
        if (responseMap == null || !responseMap.containsKey("REC")) {
            logger.error("카드 조회 결과가 비어 있음");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "카드 조회 결과가 없습니다.");
        }

        List<Map<String, Object>> recList = (List<Map<String, Object>>) responseMap.get("REC");

        // recList의 모든 항목에 대해 DB에 저장 또는 업데이트
        for (Map<String, Object> rec : recList) {
            try {
                String cardNo = (String) rec.get("cardNo");
                String cardName = (String) rec.get("cardName");
                String cardType = (String) rec.get("cardType");
                String issuerCode = (String) rec.get("issuerCode");
                // CVC 추출 추가
                String cvc = (String) rec.get("cvc");

                Optional<CardInfoEntity> existingOpt = cardInfoRepository.findByCardNo(cardNo);
                if (existingOpt.isEmpty()) {
                    CardInfoEntity card = CardInfoEntity.builder()
                            .userId(userId)
                            .cardName(cardName)
                            .cardNo(cardNo)
                            .cardType(cardType)
                            .cvc(cvc)  // CVC 설정
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    cardInfoRepository.save(card);
                    logger.info("새로운 카드 저장됨: {}", cardNo);
                } else {
                    CardInfoEntity existing = existingOpt.get();
                    existing.setCardName(cardName);
                    existing.setCardType(cardType);
                    existing.setUpdatedAt(LocalDateTime.now());
                    cardInfoRepository.save(existing);
                    logger.info("카드 정보 업데이트됨: {}", cardNo);
                }
            } catch (Exception e) {
                logger.error("카드 동기화 중 오류 발생: {}", e.getMessage());
            }
        }

        logger.info("카드 정보 동기화 완료");

        // 사용자의 모든 카드 정보를 DTO로 변환해서 반환
        List<CardInfoEntity> userCards = cardInfoRepository.findByUserId(userId);
        List<CardResponseDto.CardInfo> cardInfos = userCards.stream()
                .map(CardResponseDto.CardInfo::fromEntity)
                .collect(Collectors.toList());

        return CardResponseDto.builder()
                .cards(cardInfos)
                .build();
    }

    /**
     * 사용자의 카드 목록 조회
     */
    @Transactional(readOnly = true)
    public CardResponseDto getUserCards(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<CardInfoEntity> userCards = cardInfoRepository.findByUserId(user.getUserId());
        List<CardResponseDto.CardInfo> cardInfos = userCards.stream()
                .map(CardResponseDto.CardInfo::fromEntity)
                .collect(Collectors.toList());

        return CardResponseDto.builder()
                .cards(cardInfos)
                .build();
    }

    // 카드 거래내역 조회
    @Transactional
    public CardTransactionResponseDto getCardTransactions(String email, String cardNo, Integer year, Integer month) {
        logger.info("카드 거래내역 조회 시작: {}", LocalDateTime.now());

        // 로그인한 사용자의 정보와 카드 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        CardInfoEntity card = cardInfoRepository.findByUserIdAndCardNo(user.getUserId(), cardNo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 카드를 찾을 수 없습니다."));

        String userKey = user.getFinanceUserKey();

        // 조회 기간 설정
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // API 요청 파라미터 구성
        String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Open API 호출 준비
        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/creditCard/inquireCreditCardTransactionList";
        LocalDateTime callTime = LocalDateTime.now();
        String transmissionDate = callTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = callTime.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + String.format("%06d", new Random().nextInt(1000000));

        Map<String, Object> header = new HashMap<>();
        header.put("apiName", "inquireCreditCardTransactionList");
        header.put("transmissionDate", transmissionDate);
        header.put("transmissionTime", transmissionTime);
        header.put("institutionCode", "00100");
        header.put("fintechAppNo", "001");
        header.put("apiServiceCode", "inquireCreditCardTransactionList");
        header.put("institutionTransactionUniqueNo", institutionTransactionUniqueNo);
        header.put("apiKey", financeApiKey);
        header.put("userKey", userKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);
        requestBody.put("cardNo", cardNo);
        requestBody.put("cvc", card.getCvc());
        requestBody.put("startDate", startDateStr);
        requestBody.put("endDate", endDateStr);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, Object> responseMap = responseEntity.getBody();

        if (responseMap == null || !responseMap.containsKey("REC")) {
            logger.error("카드 거래내역 조회 결과가 비어 있음");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "카드 거래내역 조회 결과가 없습니다.");
        }

        // 응답에서 REC 객체와 거래 목록 추출
        Map<String, Object> rec = (Map<String, Object>) responseMap.get("REC");
        List<Map<String, Object>> transactionList = (List<Map<String, Object>>) rec.get("transactionList");

        if (transactionList == null || transactionList.isEmpty()) {
            logger.info("해당 기간에 거래 내역이 없습니다.");
            return CardTransactionResponseDto.builder()
                    .year(year)
                    .month(month)
                    .totalSpent(0)
                    .transactions(new ArrayList<>())
                    .build();
        }

        // REC에서 카드 정보 추출
        String cardIssuerCode = (String) rec.get("cardIssuerCode");
        String cardIssuerName = (String) rec.get("cardIssuerName");
        String updatedCardName = (String) rec.get("cardName");

        // 카드 정보 업데이트가 필요하면 수행
        if (!updatedCardName.equals(card.getCardName())) {
            card.setCardName(updatedCardName);
            card.setUpdatedAt(LocalDateTime.now());
            cardInfoRepository.save(card);
            logger.info("카드 정보 업데이트: {}", cardNo);
        }

        // DB에 저장하고 응답 생성
        List<PaymentHistoryEntity> savedTransactions = new ArrayList<>();
        int totalAmount = 0;

        for (Map<String, Object> transaction : transactionList) {
            try {
                // 거래내역 정보 추출
                String transactionUniqueNo = (String) transaction.get("transactionUniqueNo");
                String categoryId = (String) transaction.get("categoryId");
                String categoryName = (String) transaction.get("categoryName");
                String merchantId = (String) transaction.get("merchantId");
                String merchantName = (String) transaction.get("merchantName");
                String transactionDateStr = (String) transaction.get("transactionDate");
                String transactionTimeStr = (String) transaction.get("transactionTime");
                String transactionBalance = (String) transaction.get("transactionBalance");
                String cardStatus = (String) transaction.get("cardStatus");
                String billStatementsYn = (String) transaction.get("billStatementsYn");
                String billStatementsStatus = (String) transaction.get("billStatementsStatus");

                // 가맹점명(merchantName)으로 카테고리 매핑 시도
                String finalCategoryId = categoryId; // 기본값은 API에서 받은 categoryId

                try {
                    if (merchantName != null && !merchantName.isEmpty()) {
                        // merchantName과 일치하는 CategoryEntity를 검색
                        List<CategoryEntity> matchingCategories = categoryRepository.findAllByMerchantName(merchantName);

                        if (!matchingCategories.isEmpty()) {
                            // 일치하는 가맹점을 찾았으면 첫 번째 카테고리의 budget_category_id를 사용
                            CategoryEntity matchingCategory = matchingCategories.get(0);
                            Integer budgetCategoryId = matchingCategory.getBudgetCategory();

                            if (budgetCategoryId != null) {
                                finalCategoryId = budgetCategoryId.toString();
                                categoryName = matchingCategory.getCategoryName(); // 카테고리 이름도 업데이트
                                logger.info("가맹점 '{}' 매핑: categoryId {} -> budgetCategoryId {}",
                                        merchantName, categoryId, finalCategoryId);
                            }
                        } else {
                            // 일치하는 가맹점이 없으면 "결제" 카테고리의 budget_category_id를 가져옴
                            // 수정: findByBudgetCategoryName -> findByCategoryName
                            Optional<BudgetCategoryEntity> defaultCategory = budgetCategoryRepository.findByCategoryName("결제");

                            if (defaultCategory.isPresent()) {
                                finalCategoryId = defaultCategory.get().getBudgetCategoryId().toString();
                                logger.info("가맹점 '{}' 매핑 없음, 기본 '결제' 카테고리 사용: budgetCategoryId {}",
                                        merchantName, finalCategoryId);
                            } else {
                                // "결제" 카테고리가 없으면 첫 번째 카테고리 사용
                                List<BudgetCategoryEntity> allBudgetCategories = budgetCategoryRepository.findAll();
                                if (!allBudgetCategories.isEmpty()) {
                                    BudgetCategoryEntity firstCategory = allBudgetCategories.get(0);
                                    finalCategoryId = firstCategory.getBudgetCategoryId().toString();
                                    logger.info("'결제' 카테고리를 찾을 수 없음, 첫 번째 카테고리 사용: budgetCategoryId {}", finalCategoryId);
                                } else {
                                    logger.warn("사용 가능한 예산 카테고리가 없음, 원본 categoryId 유지: {}", categoryId);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("카테고리 매핑 중 오류 발생: {}", e.getMessage());
                    // 오류 발생 시 원본 categoryId 유지
                }

                // categoryId가 null이면 기본값 설정 (NOT NULL 제약조건 때문)
                if (finalCategoryId == null || finalCategoryId.trim().isEmpty()) {
                    finalCategoryId = "UNKNOWN";
                    logger.warn("거래에 categoryId가 없어 기본값 설정: {}", transactionUniqueNo);
                }

                // 날짜 변환
                LocalDate transactionDate = LocalDate.parse(
                        transactionDateStr,
                        DateTimeFormatter.ofPattern("yyyyMMdd")
                );

                // 해당 기간에 속하는 거래만 필터링
                if (transactionDate.isBefore(startDate) || transactionDate.isAfter(endDate)) {
                    continue;
                }

                // 기존 거래내역이 있는지 확인
                Optional<PaymentHistoryEntity> existingTransaction =
                        paymentHistoryRepository.findByTransactionUniqueNo(transactionUniqueNo);

                if (existingTransaction.isPresent()) {
                    // 복합 키로 인해 업데이트가 복잡하므로 삭제 후 재생성
                    paymentHistoryRepository.delete(existingTransaction.get());
                    logger.info("기존 거래내역 삭제: {}", transactionUniqueNo);
                }

                // 새 거래내역 생성
                PaymentHistoryEntity paymentHistory = PaymentHistoryEntity.builder()
                        .categoryId(finalCategoryId) // 매핑된 budgetCategoryId 또는 기본 "결제" 카테고리 ID
                        .categoryName(categoryName)
                        .merchantId(merchantId)
                        .merchantName(merchantName)
                        .transactionDate(transactionDate)
                        .transcationTime(transactionTimeStr)
                        .transactionType("") // 필요한 경우 채워넣기
                        .transactionBalance(transactionBalance)
                        .isWaste(0) // 기본값
                        .transactionUniqueNo(transactionUniqueNo)
                        .cardNo(cardNo)
                        .cardName(updatedCardName)
                        .cardIssuerCode(cardIssuerCode)
                        .cardIssuerName(cardIssuerName)
                        .cardStatus(cardStatus)
                        .billStatementsYn(billStatementsYn)
                        .billStatementsStatus(billStatementsStatus)
                        .cardId(card.getCardId())
                        .build();

                try {
                    paymentHistory = paymentHistoryRepository.save(paymentHistory);
                    savedTransactions.add(paymentHistory);
                    logger.info("거래내역 저장 성공: {}", transactionUniqueNo);

                    // 총액 계산
                    int amount = Math.abs(Integer.parseInt(transactionBalance.trim().replace(",", "")));
                    totalAmount += amount;
                } catch (Exception e) {
                    logger.error("거래내역 저장 중 데이터베이스 오류: {} - {}", transactionUniqueNo, e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error("거래내역 처리 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        // 응답 DTO 생성
        List<CardTransactionResponseDto.Transaction> transactionDtos = savedTransactions.stream()
                .map(CardTransactionResponseDto.Transaction::fromEntity)
                .collect(Collectors.toList());

        logger.info("카드 거래내역 조회 및 저장 완료: {} 건", savedTransactions.size());

        return CardTransactionResponseDto.builder()
                .year(year)
                .month(month)
                .totalSpent(totalAmount)
                .transactions(transactionDtos)
                .build();
    }

    /**
     * 사용자의 모든 카드 거래내역 조회
     */
    @Transactional
    public CardTransactionResponseDto getAllCardTransactions(String email, Integer year, Integer month) {
        logger.info("사용자 모든 카드 거래내역 조회 시작: {}", LocalDateTime.now());

        // 로그인한 사용자의 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Integer userId = user.getUserId();

        // 사용자의 모든 카드 조회
        List<CardInfoEntity> userCards = cardInfoRepository.findByUserId(userId);

        if (userCards.isEmpty()) {
            logger.info("사용자의 카드가 없습니다.");
            return CardTransactionResponseDto.builder()
                    .year(year)
                    .month(month)
                    .totalSpent(0)
                    .transactions(new ArrayList<>())
                    .build();
        }

        // 각 카드에 대해 API를 호출하여 거래내역 조회 및 통합
        List<CardTransactionResponseDto.Transaction> allTransactions = new ArrayList<>();
        int totalAmount = 0;

        for (CardInfoEntity card : userCards) {
            try {
                // 각 카드별로 거래내역 API 호출
                CardTransactionResponseDto cardTransactions =
                        getCardTransactions(email, card.getCardNo(), year, month);

                // 거래내역 및 금액 합산
                allTransactions.addAll(cardTransactions.getTransactions());
                totalAmount += cardTransactions.getTotalSpent();

            } catch (Exception e) {
                logger.error("카드 {} 거래내역 조회 중 오류 발생: {}", card.getCardNo(), e.getMessage(), e);
            }
        }

        return CardTransactionResponseDto.builder()
                .year(year)
                .month(month)
                .totalSpent(totalAmount)
                .transactions(allTransactions)
                .build();
    }
}