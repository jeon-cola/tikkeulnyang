package com.c107.accounts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @Column(nullable = false)
    private Integer userId;

    // 계좌 번호
    @Column(nullable = false, length = 50)
    private String accountNumber;

    // 은행 코드 혹은 은행 이름 (필요에 따라 수정)
    @Column(nullable = false, length = 50)
    private String bankName;

    // 계좌 잔액 (초기에는 0으로 설정)
    @Column(nullable = false, length = 255)
    private String balance;


    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "account_type")
    private String accountType;

    @Column(nullable = false)
    private boolean representative;
}
