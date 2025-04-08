package com.c107.accounts.service;

import com.c107.accounts.dto.ServiceTransactionDto;
import com.c107.accounts.entity.Account;
import com.c107.accounts.entity.ServiceTransaction;
import com.c107.accounts.repository.AccountRepository;
import com.c107.accounts.repository.AccountTransactionRepository;
import com.c107.accounts.repository.ServiceTransactionRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import com.c107.transactions.entity.Transaction;
import com.c107.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class AccountService {

    @Value("${finance.api.key}")
    private String financeApiKey;

    @Value("${service.account.number}")
    private String serviceAccount;

    @Value("${service.user.key}")
    private String serviceUserKey;

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final ServiceTransactionRepository serviceTransactionRepository;

    // PasswordEncoder 빈 주입
    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Open API를 호출하여 사용자의 모든 계좌 정보를 DB에 등록 또는 업데이트
     */
    @Transactional
    public List<Account> refreshAccounts(Integer loggedInUserId) {
        logger.info("수동 계좌 동기화 시작: {}", LocalDateTime.now());

        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));

        String userKey = user.getFinanceUserKey();
        logger.debug("finance.api.key: {}", financeApiKey);
        logger.debug("사용자 financeUserKey: {}", userKey);

        String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireDemandDepositAccountList";
        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime
                + String.format("%06d", new Random().nextInt(1000000));

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

        logger.debug("API 요청 URL: {}", url);
        logger.debug("API 요청 Body: {}", requestBody);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, Object> responseMap = responseEntity.getBody();

        logger.debug("API 응답 상태: {}", responseEntity.getStatusCode());
        logger.debug("API 응답 Body: {}", responseMap);

        if (responseMap == null || !responseMap.containsKey("REC")) {
            logger.error("계좌 조회 결과가 비어 있음");
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌 조회 결과가 없습니다.");
        }

        List<Map<String, Object>> recList = (List<Map<String, Object>>) responseMap.get("REC");

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
                logger.error("계좌 동기화 중 오류 발생: {}", e.getMessage(), e);
            }
        }
        logger.info("수동 계좌 동기화 완료");
        return accountRepository.findByUserId(loggedInUserId);
    }

    /**
     * 거래내역 가져오기 (생략 - 기존 코드와 동일)
     */
    @Transactional
    public void syncNewTransactions(Integer loggedInUserId, Integer userSelectedCategoryId) {
        logger.info("신규 거래내역 동기화 시작: {}", LocalDateTime.now());

        // 사용자 정보 조회 (금융 API용 userKey)
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        String userKey = user.getFinanceUserKey();

        // 해당 유저의 모든 등록된 계좌 조회
        List<Account> accounts = accountRepository.findByUserId(loggedInUserId);
        if (accounts.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "등록된 계좌가 없습니다.");
        }

        // 각 계좌마다 진행
        for (Account account : accounts) {
            String accountNo = account.getAccountNumber();

            // 각 계좌의 transactions 테이블에서 마지막 거래일 조회
            Optional<Transaction> lastTxOpt = transactionRepository.findTopByAccountIdOrderByTransactionDateDesc(accountNo);
            LocalDate startDate;
            if (lastTxOpt.isPresent()) {
                // 마지막 거래일 이후 날짜(예: 다음날)로 설정
                startDate = lastTxOpt.get().getTransactionDate().toLocalDate().plusDays(1);
            } else {
                // 거래내역이 없는 경우, 기본 조회 시작일 (예: 최근 7일 전)
                startDate = LocalDate.now().minusDays(7);
            }
            // yyyyMMdd 형식 문자열로 변환
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Open API 호출 준비
            String url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireTransactionHistoryList";
            LocalDateTime now = LocalDateTime.now();
            String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
            String institutionTransactionUniqueNo = transmissionDate + transmissionTime
                    + String.format("%06d", new Random().nextInt(1000000));

            Map<String, Object> header = new HashMap<>();
            header.put("apiName", "inquireTransactionHistoryList");
            header.put("transmissionDate", transmissionDate);
            header.put("transmissionTime", transmissionTime);
            header.put("institutionCode", "00100");
            header.put("fintechAppNo", "001");
            header.put("apiServiceCode", "inquireTransactionHistoryList");
            header.put("institutionTransactionUniqueNo", institutionTransactionUniqueNo);
            header.put("apiKey", financeApiKey);
            header.put("userKey", userKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("Header", header);
            requestBody.put("accountNo", accountNo);
            requestBody.put("startDate", startDateStr);
            requestBody.put("endDate", endDateStr);
            // 거래 유형 및 정렬 방식은 고정값("A", "ASC")로 가정
            requestBody.put("transactionType", "A");
            requestBody.put("orderByType", "ASC");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
            Map<String, Object> responseMap = responseEntity.getBody();
            if (responseMap == null || !responseMap.containsKey("REC")) {
                logger.error("계좌 {}에 대한 거래내역 조회 결과가 비어 있음", accountNo);
                continue;
            }

            // 응답 데이터 파싱 (REC 내부의 list 필드)
            Map<String, Object> rec = (Map<String, Object>) responseMap.get("REC");
            List<Map<String, Object>> transactionList = (List<Map<String, Object>>) rec.get("list");

            // 각 거래내역에 대해 transactions 테이블에 저장 (입금, 출금 모두 처리)
            for (Map<String, Object> tx : transactionList) {
                try {
                    // 거래 데이터를 파싱
                    String txDate = (String) tx.get("transactionDate"); // yyyyMMdd
                    String txTime = (String) tx.get("transactionTime");   // HHmmss
                    String txTypeCode = (String) tx.get("transactionType"); // "1" 또는 "2"
                    String txAccountNo = (String) tx.get("transactionAccountNo");
                    String txBalanceStr = (String) tx.get("transactionBalance");
                    String txAfterBalanceStr = (String) tx.get("transactionAfterBalance");

                    LocalDateTime txDateTime = LocalDateTime.parse(txDate + txTime,
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    int txBalance = Integer.parseInt(txBalanceStr);
                    int txAfterBalance = Integer.parseInt(txAfterBalanceStr);
                    int beforeBalance = txAfterBalance - txBalance;

                    // 중복 거래 체크: 이미 같은 account_id, transaction_date, transaction_type, amount인 거래가 존재하면 건너뜁니다.
                    Optional<Transaction> existingTx = transactionRepository.findTopByAccountIdAndTransactionDateAndTransactionTypeAndAmount(
                            String.valueOf(account.getAccountId()), txDateTime, Integer.parseInt(txTypeCode), txBalance);
                    if (existingTx.isPresent()) {
                        continue;
                    }

                    Transaction transaction = Transaction.builder()
                            .cardId(0) // 카드 관련 정보 없음
                            .transactionDate(txDateTime)
                            .categoryId(userSelectedCategoryId)
                            .amount(txBalance)
                            .accountId(String.valueOf(account.getAccountId()))
                            // user_id 추가
                            .userId(loggedInUserId)
                            .transactionAccountNo(txAccountNo)
                            .transactionType(Integer.parseInt(txTypeCode))
                            .accountBeforeTransaction(beforeBalance)
                            .accountAfterTransaction(txAfterBalance)
                            .isWaste(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .deleted(0)
                            .build();
                    transactionRepository.save(transaction);
                    logger.info("Transaction 저장됨, 계좌: {}, 거래일시: {}, 금액: {}",
                            accountNo, txDateTime, txBalance);
                } catch (Exception e) {
                    logger.error("계좌 {}의 거래내역 동기화 중 오류 발생: {}", accountNo, e.getMessage(), e);
                }
            }
        }
        logger.info("신규 거래내역 동기화 완료");
        // 해당 부분은 기존 코드를 그대로 사용합니다.
    }

    /**
     * 예치금 충전 (대표계좌에서 서비스 계좌로 이체) 처리
     * 거래 비밀번호를 검증한 후 진행합니다.
     */
    @Transactional
    public void depositCharge(Integer loggedInUserId, String amount, String transactionPassword) {
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));

        // 거래 비밀번호 검증
        if (transactionPassword == null || !passwordEncoder.matches(transactionPassword, user.getTransactionPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "거래 비밀번호가 올바르지 않습니다.");
        }

        Optional<Account> repOpt = accountRepository.findByUserIdAndRepresentativeTrue(loggedInUserId);
        if (repOpt.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "대표계좌가 설정되어 있지 않습니다.");
        }
        Account representativeAccount = repOpt.get();
        String representativeAccountNo = representativeAccount.getAccountNumber();
        int repBalance = Integer.parseInt(representativeAccount.getBalance());
        int depositAmt = Integer.parseInt(amount);
        if (depositAmt > repBalance) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "계좌잔액이 부족합니다.");
        }

        Map<String, Object> response = transferDeposit(loggedInUserId, serviceAccount, representativeAccountNo, amount, false);
        logger.info("예치금 이체 API 응답: {}", response);

        int newRepBalance = repBalance - depositAmt;
        representativeAccount.setBalance(String.valueOf(newRepBalance));
        accountRepository.save(representativeAccount);
        logger.info("대표계좌 잔액 업데이트 완료: {} → {}", repBalance, newRepBalance);

        ServiceTransaction transaction = ServiceTransaction.builder()
                .accountId(getServiceAccountId())
                .userId(loggedInUserId)
                .transactionDate(LocalDateTime.now())
                .category("DEPOSIT_CHARGE")
                .transactionType("DEPOSIT")
                .transactionAccountNo(representativeAccountNo)
                .transactionBalance(depositAmt)
                .description("예치금 충전: 대표 계좌 " + representativeAccountNo + " → 서비스 계좌 " + serviceAccount)
                .transactionAfterBalance(newRepBalance)
                .build();
        accountTransactionRepository.save(transaction);
        logger.info("예치금 충전 거래내역 기록됨: {}", transaction);

        int currentDeposit = (user.getDeposit() != null ? user.getDeposit() : 0);
        int newDeposit = currentDeposit + depositAmt;
        user.setDeposit(newDeposit);
        userRepository.save(user);
        logger.info("사용자 예치금 업데이트 완료: {}", newDeposit);

        List<Account> updatedAccounts = refreshAccounts(loggedInUserId);
        logger.info("계좌 잔액 동기화 완료: {}", updatedAccounts);
    }

    /**
     * 예치금 환불 (생략 - 기존 코드와 동일)
     */
    @Transactional
    public void refundDeposit(Integer loggedInUserId, String amount) {
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        int currentDeposit = (user.getDeposit() != null ? user.getDeposit() : 0);
        int refundAmt = (int) Long.parseLong(amount);
        if (refundAmt > currentDeposit) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "환불 요청 금액이 예치금보다 많습니다.");
        }

        Optional<Account> repOpt = accountRepository.findByUserIdAndRepresentativeTrue(loggedInUserId);
        if (repOpt.isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "대표계좌가 설정되어 있지 않습니다.");
        }
        Account representativeAccount = repOpt.get();
        String representativeAccountNo = representativeAccount.getAccountNumber();

        Map<String, Object> response = transferDeposit(loggedInUserId, representativeAccountNo, serviceAccount, amount, true);
        logger.info("환불 이체 API 응답: {}", response);

        int repBalance = Integer.parseInt(representativeAccount.getBalance());
        int newRepBalance = repBalance + refundAmt;
        representativeAccount.setBalance(String.valueOf(newRepBalance));
        accountRepository.save(representativeAccount);
        logger.info("대표계좌 잔액 업데이트 완료: {} → {}", repBalance, newRepBalance);

        ServiceTransaction transaction = ServiceTransaction.builder()
                .accountId(getServiceAccountId())
                .userId(loggedInUserId)
                .transactionDate(LocalDateTime.now())
                .category("DEPOSIT_REFUND")
                .transactionType("REFUND")
                .transactionAccountNo(representativeAccountNo)
                .transactionBalance(refundAmt)
                .description("예치금 환불: 서비스 계좌 " + serviceAccount + " → 대표 계좌 " + representativeAccountNo)
                .transactionAfterBalance(newRepBalance)
                .build();
        accountTransactionRepository.save(transaction);
        logger.info("예치금 환불 거래내역 기록됨: {}", transaction);

        int newDeposit = currentDeposit - refundAmt;
        user.setDeposit(newDeposit);
        userRepository.save(user);
        logger.info("사용자 예치금 환불 후 업데이트 완료: {}", newDeposit);

        List<Account> updatedAccounts = refreshAccounts(loggedInUserId);
        logger.info("계좌 잔액 동기화 완료: {}", updatedAccounts);
        // 예치금 환불 처리 기존 코드를 그대로 사용합니다.
    }

    /**
     * 예치금 이체 Open API 호출 (transferDeposit)
     */
    @Transactional
    public Map<String, Object> transferDeposit(Integer loggedInUserId,
                                               String depositAccountNo,
                                               String withdrawalAccountNo,
                                               String transactionBalance,
                                               boolean isRefund) {
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보가 존재하지 않습니다."));
        // 환불인 경우 지정된 userKey, 아니면 로그인한 사용자의 userKey 사용
        String userKey = isRefund ? serviceUserKey : user.getFinanceUserKey();
        logger.warn("transferDeposit userKey 사용 값: {}", userKey);

        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime
                + String.format("%06d", new Random().nextInt(1000000));

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
     * 등록된 계좌 중 대표계좌 설정 기능
     */
    @Transactional
    public void setRepresentativeAccount(Integer loggedInUserId, String accountNo) {
        List<Account> accounts = accountRepository.findByUserId(loggedInUserId);
        boolean exists = accounts.stream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNo));
        if (!exists) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "해당 계좌번호는 로그인한 사용자의 계좌가 아닙니다.");
        }
        for (Account account : accounts) {
            account.setRepresentative(account.getAccountNumber().equals(accountNo));
            accountRepository.save(account);
        }
        logger.info("대표계좌 설정 완료: {}", accountNo);
    }

    /**
     * 서비스 계좌 ID 가져오기 헬퍼 메서드
     */
    public Integer getServiceAccountId() {
        Optional<Account> serviceAccount = accountRepository.findByUserIdIsNullAndAccountType("SERVICE");
        if (serviceAccount.isPresent()) {
            return serviceAccount.get().getAccountId();
        }
        throw new CustomException(ErrorCode.NOT_FOUND, "서비스 계좌가 등록되어 있지 않습니다.");
    }

    /**
     * 계좌 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAccountList(Integer userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(account -> {
                    Map<String, Object> accountMap = new HashMap<>();
                    accountMap.put("account_id", account.getAccountId());
                    accountMap.put("bank_name", account.getBankName());
                    accountMap.put("account_number", account.getAccountNumber());
                    accountMap.put("balance", account.getBalance());
                    accountMap.put("account_type", account.getAccountType());
                    accountMap.put("representative", account.isRepresentative());
                    return accountMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * 내역 조회
     */
    @Transactional(readOnly = true)
    public List<ServiceTransactionDto> getServiceTransactions(Integer userId) {
        List<String> validCategories = Arrays.asList(
                "DEPOSIT_CHARGE",
                "DEPOSIT_REFUND",
                "CHALLENGE_JOIN",
                "CHALLENGE_DELETE_REFUND",
                "CHALLENGE_SETTLE_REFUND"
        );
        List<ServiceTransaction> transactions = serviceTransactionRepository.findByUserIdAndCategoryIn(userId, validCategories);
        return transactions.stream()
                .map(tx -> ServiceTransactionDto.builder()
                        .transactionDate(tx.getTransactionDate())
                        .category(tx.getCategory())
                        .description(tx.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
