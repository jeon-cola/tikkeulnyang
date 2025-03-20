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
    public List<Account> refreshAccounts(Integer loggedInUserId) {
        logger.info("수동 계좌 동기화 시작: {}", LocalDateTime.now());

        // 로그인한 사용자의 정보를 DB에서 조회하여 financeUserKey를 가져옴
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        String userKey = user.getFinanceUserKey();

        // Open API 호출 준비
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
        // responseMap에 응답 본문 저장
        Map<String, Object> responseMap = responseEntity.getBody();
        if (responseMap == null || !responseMap.containsKey("REC")) {
            logger.error("계좌 조회 결과가 비어 있음");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌 조회 결과가 없습니다.");
        }

        List<Map<String, Object>> recList = (List<Map<String, Object>>) responseMap.get("REC");

        // DB에서 해당 사용자의 계좌가 존재하는지 확인 (가장 오래된 계좌 기준)
        Optional<Account> optionalAccount = accountRepository.findFirstByUserIdOrderByCreatedAtAsc(loggedInUserId);
        if (optionalAccount.isEmpty()) {
            // DB에 계좌가 없으면, open API 응답의 첫 번째 계좌 정보를 사용해 신규 계좌 생성
            Map<String, Object> firstRec = recList.get(0);
            String accountNo = (String) firstRec.get("accountNo");
            String bankCode = (String) firstRec.get("bankCode");
            Object currencyObj = firstRec.get("currency");
            String currency = (currencyObj instanceof Map)
                    ? (String) ((Map) currencyObj).get("currency")
                    : (String) currencyObj;
            String accountBalance = (String) firstRec.get("accountBalance");

            Account newAccount = Account.builder()
                    .userId(loggedInUserId)
                    .accountNumber(accountNo)
                    .bankName(bankCode)
                    .balance(accountBalance)
                    .currency(currency)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            accountRepository.save(newAccount);
            logger.info("새로운 계좌 저장됨: {}", accountNo);
        } else {
            // 기존 계좌가 있으면, 응답의 각 REC 항목에 대해 계좌 정보 업데이트 또는 신규 생성
            for (Map<String, Object> rec : recList) {
                try {
                    String accountNo = (String) rec.get("accountNo");
                    String bankName = (String) rec.get("bankName");
                    Object currencyObj = rec.get("currency");
                    String currency = (currencyObj instanceof Map)
                            ? (String) ((Map) currencyObj).get("currency")
                            : (String) currencyObj;
                    String accountBalance = (String) rec.get("accountBalance");

                    Optional<Account> existingOpt = accountRepository.findByAccountNumber(accountNo);
                    if (existingOpt.isEmpty()) {
                        Account account = Account.builder()
                                .userId(loggedInUserId)
                                .accountNumber(accountNo)
                                .bankName(bankName)
                                .balance(accountBalance)
                                .currency(currency)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        accountRepository.save(account);
                        logger.info("새로운 계좌 저장됨: {}", accountNo);
                    } else {
                        Account existing = existingOpt.get();
                        existing.setBalance(accountBalance);
                        existing.setUpdatedAt(LocalDateTime.now());
                        accountRepository.save(existing);
                        logger.info("계좌 업데이트됨: {}", accountNo);
                    }
                } catch (Exception e) {
                    logger.error("계좌 동기화 중 오류 발생: {}", e.getMessage());
                }
            }
        }

        logger.info("수동 계좌 동기화 완료");
        return accountRepository.findAll();
    }
}
