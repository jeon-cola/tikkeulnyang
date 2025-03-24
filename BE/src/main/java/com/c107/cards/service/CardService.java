package com.c107.cards.service;

import com.c107.cards.dto.CardResponseDto;
import com.c107.cards.entity.CardInfoEntity;
import com.c107.cards.repository.CardInfoRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
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

                Optional<CardInfoEntity> existingOpt = cardInfoRepository.findByCardNo(cardNo);
                if (existingOpt.isEmpty()) {
                    CardInfoEntity card = CardInfoEntity.builder()
                            .userId(userId)
                            .cardName(cardName)
                            .cardNo(cardNo)
                            .cardType(cardType)
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
}