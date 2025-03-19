package com.c107.accounts.service;

import com.c107.accounts.entity.Account;
import com.c107.accounts.repository.AccountRepository;
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

@Service
@RequiredArgsConstructor
public class AccountService {
    @Value("${finance.api.key}")
    private String financeApiKey;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public List<Account> refreshAccounts(String userKey, String accountTypeUniqueNo) {
        logger.info("수동 계좌 동기화 시작: {}", LocalDateTime.now());

        // userKey를 이용해 사용자 정보 조회 (User 엔티티의 financeUserKey 필드에 저장된 값)
        User user = userRepository.findByFinanceUserKey(userKey)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        Integer userId = user.getUserId();

        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireDemandDepositAccountList";
        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + String.format("%06d", new Random().nextInt(1000000));

        Map<String, Object> header = new HashMap<>();
        header.put("apiName", "inquireDemandDepositAccountList");
        header.put("transmissionDate", transmissionDate);
        header.put("transmissionTime", transmissionTime);
        header.put("institutionCode", "00100");
        header.put("fintechAppNo", "001");
        header.put("apiServiceCode", "inquireDemandDepositAccountList");
        header.put("institutionTransactionUniqueNo", institutionTransactionUniqueNo);
        header.put("apiKey", financeApiKey);
        header.put("userKey", userKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("계좌 조회 실패, 응답 코드: {}", responseEntity.getStatusCode());
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌 조회에 실패했습니다.");
        }

        Map responseMap = responseEntity.getBody();
        if (responseMap == null || !responseMap.containsKey("REC")) {
            logger.error("계좌 조회 결과가 비어 있음");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌 조회 결과가 없습니다.");
        }

        List<Map<String, Object>> recList = (List<Map<String, Object>>) responseMap.get("REC");
        for (Map<String, Object> rec : recList) {
            try {
                String accountNo = (String) rec.get("accountNo");
                String bankCode = (String) rec.get("bankCode");
                Object currencyObj = rec.get("currency");
                String currency;
                if (currencyObj instanceof Map) {
                    currency = (String) ((Map) currencyObj).get("currency");
                } else {
                    currency = (String) currencyObj;
                }
                String accountBalance = (String) rec.get("accountBalance");

                Account existing = accountRepository.findByAccountNumber(accountNo).orElse(null);
                if (existing == null) {
                    Account account = Account.builder()
                            .userId(userId)  // userId를 반드시 설정
                            .accountNumber(accountNo)
                            .bankName(bankCode)
                            .balance(accountBalance)
                            .currency(currency)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    accountRepository.save(account);
                    logger.info("새로운 계좌 저장됨: {}", accountNo);
                } else {
                    existing.setBalance(accountBalance);
                    existing.setUpdatedAt(LocalDateTime.now());
                    accountRepository.save(existing);
                    logger.info("계좌 업데이트됨: {}", accountNo);
                }
            } catch (Exception e) {
                logger.error("계좌 동기화 중 오류 발생: {}", e.getMessage());
            }
        }
        logger.info("수동 계좌 동기화 완료");
        return accountRepository.findAll();
    }
}
