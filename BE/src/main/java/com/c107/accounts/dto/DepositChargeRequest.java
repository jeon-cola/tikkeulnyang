package com.c107.accounts.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositChargeRequest {
    // 사용자가 선택한 계좌번호 (출금계좌)
    private String selectedAccountNo;
    // 충전할 금액 (문자열 형식, 필요에 따라 int 등으로 변환)
    private String amount;
}
