package com.c107.accounts.service;

import com.c107.accounts.entity.Account;
import com.c107.accounts.repository.AccountRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Open API를 호출하여 계좌를 생성하고, 응답 결과를 로컬 DB에 저장.
     *
     * @param userKey               Open API에서 발급받은 사용자 키
     * @param userId                로컬 사용자 ID
     * @param accountTypeUniqueNo   계좌 유형 고유번호 (외부 API 요청에 사용)
     * @return 생성된 Account 엔티티
     */
    public Account syncAccountFromOpenAPI(String userKey, Integer userId, String accountTypeUniqueNo) {
        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/createDemandDepositAccount";

        // 요청 JSON 구성
        Map<String, Object> header = new HashMap<>();
        header.put("apiName", "createDemandDepositAccount");
        header.put("transmissionDate", "20250313");  // 예시 값
        header.put("transmissionTime", "133700");     // 예시 값
        header.put("institutionCode", "00100");
        header.put("fintechAppNo", "001");
        header.put("apiServiceCode", "createDemandDepositAccount");
        header.put("institutionTransactionUniqueNo", "20240215121212123457");
        header.put("apiKey", "4fd56093078f45a6b95a1e77a1334282");
        header.put("userKey", userKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);
        requestBody.put("accountTypeUniqueNo", accountTypeUniqueNo);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        // Open API 호출
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌 생성 실패");
        }
        Map responseMap = responseEntity.getBody();
        Map rec = (Map) responseMap.get("REC");
        // 응답에서 필요한 정보 추출
        String bankCode = (String) rec.get("bankCode");  // 예: 은행 코드
        String accountNo = (String) rec.get("accountNo");
        Map currencyMap = (Map) rec.get("currency");
        String currency = (String) currencyMap.get("currency");

        // Account 엔티티 생성 및 저장 (초기 잔액은 0으로 설정)
        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNo)
                .bankName(bankCode)  // 은행 코드를 bankName 필드에 저장 (필요에 따라 변경 가능)
                .balance("0")
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return accountRepository.save(account);
    }
}
