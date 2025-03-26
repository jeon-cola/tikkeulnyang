package com.c107.accounts.service;

import com.c107.accounts.entity.Account;
import com.c107.accounts.entity.ServiceTransaction;
import com.c107.accounts.repository.AccountRepository;
import com.c107.accounts.repository.AccountTransactionRepository;
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
    private final AccountTransactionRepository accountTransactionRepository; // 내부 거래내역 기록용
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Open API를 호출하여 사용자의 모든 계좌 정보를 DB에 등록 또는 업데이트
     */
    @Transactional
    public List<Account> refreshAccounts(Integer loggedInUserId) {
        logger.info("수동 계좌 동기화 시작: {}", LocalDateTime.now());

        // 로그인한 사용자의 정보를 DB에서 조회하여 financeUserKey 확보
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

        // recList의 모든 항목에 대해 DB에 저장 또는 업데이트
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
                            // 대표계좌 플래그는 기본 false로 설정 (추후 별도 설정)
                            .representative(false)
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

        logger.info("수동 계좌 동기화 완료");
        return accountRepository.findByUserId(loggedInUserId);
    }

    /**
     * 예치금 충전 (대표계좌에서 서비스 계좌로 이체) 처리
     * 로그인한 사용자의 대표계좌를 DB에서 조회하여, 서비스 계좌(고정 값)로 이체 요청을 진행합니다.
     * @param loggedInUserId 사용자 ID
     * @param amount 이체할 금액 (예치금 충전액)
     */
    @Transactional
    public void depositCharge(Integer loggedInUserId, String amount) {
        // 서비스 계좌번호 (고정 값)
        String serviceAccountNo = "0018031273647742";

        // 로그인한 사용자의 대표계좌 조회
        Optional<Account> repOpt = accountRepository.findByUserIdAndRepresentativeTrue(loggedInUserId);
        if (repOpt.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "대표계좌가 설정되어 있지 않습니다.");
        }
        Account representativeAccount = repOpt.get();
        String representativeAccountNo = representativeAccount.getAccountNumber();

        // Open API 호출로 이체 진행 (서비스 계좌 → 대표계좌로 충전)
        Map<String, Object> response = transferDeposit(loggedInUserId, serviceAccountNo, representativeAccountNo, amount);
        logger.info("예치금 이체 API 응답: {}", response);

        // 대표계좌 잔액 직접 업데이트: 충전의 경우, 대표계좌에서 출금되므로 잔액 감소
        int repBalance = Integer.parseInt(representativeAccount.getBalance());
        int depositAmt = Integer.parseInt(amount);
        int newRepBalance = repBalance - depositAmt;
        representativeAccount.setBalance(String.valueOf(newRepBalance));
        accountRepository.save(representativeAccount);
        logger.info("대표계좌 잔액 업데이트 완료: 이전 잔액={} → 새로운 잔액={}", repBalance, newRepBalance);

        // 최신 잔액을 거래 내역에 기록 (업데이트된 대표계좌 잔액 사용)
        ServiceTransaction transaction = ServiceTransaction.builder()
                .accountId(getServiceAccountId()) // 서비스 계좌의 ID (DB에서 조회)
                .userId(loggedInUserId)
                .transactionDate(LocalDateTime.now())
                .category("DEPOSIT_CHARGE")
                .transactionType("DEPOSIT")
                .transactionAccountNo(representativeAccountNo)
                .transactionBalance(depositAmt)
                .description("예치금 충전: 대표 계좌 " + representativeAccountNo + " → 서비스 계좌 " + serviceAccountNo)
                .transactionAfterBalance(newRepBalance)
                .build();
        accountTransactionRepository.save(transaction);
        logger.info("예치금 충전 거래내역 기록됨: {}", transaction);

        // 사용자 예치금 업데이트: 충전액 만큼 증가
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        int currentDeposit = (user.getDeposit() != null ? user.getDeposit() : 0);
        int newDeposit = currentDeposit + depositAmt;
        user.setDeposit(newDeposit);
        userRepository.save(user);
        logger.info("사용자 예치금 업데이트 완료: {}", newDeposit);

        // (옵션) 계좌 잔액 동기화 호출
        List<Account> updatedAccounts = refreshAccounts(loggedInUserId);
        logger.info("계좌 잔액 동기화 완료: {}", updatedAccounts);
    }

    /**
     * 예치금 환불 (서비스 계좌에서 사용자 대표계좌로 이체) 처리
     * 환불 요청 금액이 사용자의 현재 예치금보다 많지 않아야 하며,
     * 외부 API를 통해 서비스 계좌에서 사용자 대표계좌로 환불 이체를 진행합니다.
     * @param loggedInUserId 사용자 ID
     * @param amount 환불할 금액 (예치금 환불액)
     */
    @Transactional
    public void refundDeposit(Integer loggedInUserId, String amount) {
        // 1. 사용자 정보 조회 및 예치금 검증
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        int currentDeposit = (user.getDeposit() != null ? user.getDeposit() : 0);
        int refundAmt = Integer.parseInt(amount);
        if (refundAmt > currentDeposit) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "환불 요청 금액이 예치금보다 많습니다.");
        }

        // 2. 로그인한 사용자의 대표계좌 조회
        Optional<Account> repOpt = accountRepository.findByUserIdAndRepresentativeTrue(loggedInUserId);
        if (repOpt.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "대표계좌가 설정되어 있지 않습니다.");
        }
        Account representativeAccount = repOpt.get();
        String representativeAccountNo = representativeAccount.getAccountNumber();

        // 3. 서비스 계좌번호 (고정 값)
        String serviceAccountNo = "0018031273647742";

        // 4. 외부 API 호출: 환불 이체 (서비스 계좌 → 대표계좌)
        //    파라미터 순서 변경: 입금 대상: 대표계좌, 출금 대상: 서비스 계좌
        Map<String, Object> response = transferDeposit(loggedInUserId, representativeAccountNo, serviceAccountNo, amount);
        logger.info("환불 이체 API 응답: {}", response);

        // 5. 대표계좌 잔액 직접 업데이트: 환불의 경우, 대표계좌 잔액이 증가
        int repBalance = Integer.parseInt(representativeAccount.getBalance());
        int newRepBalance = repBalance + refundAmt;
        representativeAccount.setBalance(String.valueOf(newRepBalance));
        accountRepository.save(representativeAccount);
        logger.info("대표계좌 잔액 업데이트 완료: 이전 잔액={} → 새로운 잔액={}", repBalance, newRepBalance);

        // 6. 최신 잔액을 거래 내역에 기록
        ServiceTransaction transaction = ServiceTransaction.builder()
                .accountId(getServiceAccountId())  // 서비스 계좌의 ID (DB에서 조회)
                .userId(loggedInUserId)
                .transactionDate(LocalDateTime.now())
                .category("DEPOSIT_REFUND")
                .transactionType("REFUND")
                .transactionAccountNo(representativeAccountNo)
                .transactionBalance(refundAmt)
                .description("예치금 환불: 서비스 계좌 " + serviceAccountNo + " → 대표 계좌 " + representativeAccountNo)
                .transactionAfterBalance(newRepBalance)
                .build();
        accountTransactionRepository.save(transaction);
        logger.info("예치금 환불 거래내역 기록됨: {}", transaction);

        // 7. 사용자 예치금 업데이트: 환불액 만큼 차감
        int newDeposit = currentDeposit - refundAmt;
        user.setDeposit(newDeposit);
        userRepository.save(user);
        logger.info("사용자 예치금 환불 후 업데이트 완료: {}", newDeposit);

        // 8. (옵션) 계좌 잔액 동기화 호출
        List<Account> updatedAccounts = refreshAccounts(loggedInUserId);
        logger.info("계좌 잔액 동기화 완료: {}", updatedAccounts);
    }

    /**
     * 예치금 이체 Open API 호출 (transferDeposit)
     */
    @Transactional
    public Map<String, Object> transferDeposit(Integer loggedInUserId,
                                               String depositAccountNo,
                                               String withdrawalAccountNo,
                                               String transactionBalance) {
        // 사용자 정보 조회 (금융 API userKey 필요)
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        String userKey = user.getFinanceUserKey();

        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + String.format("%06d", new Random().nextInt(1000000));

        Map<String, Object> header = new HashMap<>();
        header.put("apiName", "updateDemandDepositAccountTransfer");
        header.put("transmissionDate", transmissionDate);
        header.put("transmissionTime", transmissionTime);
        header.put("institutionCode", "00100");
        header.put("fintechAppNo", "001");
        header.put("apiServiceCode", "updateDemandDepositAccountTransfer");
        header.put("institutionTransactionUniqueNo", institutionTransactionUniqueNo);
        header.put("apiKey", financeApiKey);
        header.put("userKey", userKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);
        requestBody.put("depositAccountNo", depositAccountNo);
        requestBody.put("depositTransactionSummary", "(수시입출금) : 입금(이체)");
        requestBody.put("transactionBalance", transactionBalance);
        requestBody.put("withdrawalAccountNo", withdrawalAccountNo);
        requestBody.put("withdrawalTransactionSummary", "(수시입출금) : 출금(이체)");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/updateDemandDepositAccountTransfer";
        logger.info("예치금 이체 요청 시작: {}", LocalDateTime.now());

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, Object> responseMap = responseEntity.getBody();

        if (responseMap == null) {
            logger.error("예치금 이체 응답이 비어 있습니다.");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "예치금 이체 응답이 비어 있습니다.");
        }
        logger.info("예치금 이체 완료: {}", responseMap);
        return responseMap;
    }

    /**
     * 등록된 계좌 중 대표계좌를 설정하는 기능
     * 사용자 계좌 목록에서 요청한 accountNo를 대표계좌(true)로 설정하고 나머지는 false로 업데이트
     */
    @Transactional
    public void setRepresentativeAccount(Integer loggedInUserId, String accountNo) {
        // 사용자 ID로 해당 사용자의 모든 계좌 조회
        List<Account> accounts = accountRepository.findByUserId(loggedInUserId);

        // 전달받은 accountNo가 사용자 계좌 목록에 있는지 확인
        boolean exists = accounts.stream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNo));
        if (!exists) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "해당 계좌번호는 로그인한 사용자의 계좌가 아닙니다.");
        }

        // 존재하는 경우, 대표계좌 설정: 전달된 계좌만 true, 나머지는 false로 업데이트
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNo)) {
                account.setRepresentative(true);
            } else {
                account.setRepresentative(false);
            }
            accountRepository.save(account);
        }
        logger.info("대표계좌 설정 완료: {}", accountNo);
    }

    // 서비스 계좌의 ID를 가져오는 로직 (필요 시 구현)
    private Integer getServiceAccountId() {
        // 서비스 계좌는 user_id가 NULL이며, account_type이 'SERVICE'로 등록되어 있다고 가정
        Optional<Account> serviceAccount = accountRepository.findByUserIdIsNullAndAccountType("SERVICE");
        if (serviceAccount.isPresent()) {
            return serviceAccount.get().getAccountId();
        }
        throw new CustomException(ErrorCode.NOT_FOUND, "서비스 계좌가 등록되어 있지 않습니다.");
    }

    // calculateAfterBalance는 대표계좌 잔액을 정수로 파싱하여 반환합니다.
    private Integer calculateAfterBalance(Integer loggedInUserId, String amount) {
        Optional<Account> repOpt = accountRepository.findByUserIdAndRepresentativeTrue(loggedInUserId);
        if (repOpt.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "대표계좌가 등록되어 있지 않습니다.");
        }
        Account representativeAccount = repOpt.get();
        try {
            return Integer.parseInt(representativeAccount.getBalance());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "잔액 정보가 올바르지 않습니다.");
        }
    }
}
