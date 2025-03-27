package com.c107.transactions.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", schema = "catcat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "card_id", nullable = false)
    private int cardId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    // 🔄 기존 category (varchar) 제거하고 categoryId 추가
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "amount", length = 255)
    private Integer amount;

    @Column(name = "account_id", length = 255)
    private String accountId;

    @Column(name = "transaction_account_no", length = 16)
    private String transactionAccountNo;

    @Column(name = "transaction_type")
    private Integer transactionType; // 1:입금, 2:출금

    @Column(name = "account_before_transaction")
    private Integer accountBeforeTransaction;

    @Column(name = "account_after_transaction")
    private Integer accountAfterTransaction;

    @Column(name = "is_waste", columnDefinition = "tinyint(1) default 0")
    private Integer isWaste;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", columnDefinition = "tinyint(1) default 0")
    private Integer deleted;

    @Column(name = "merchant_name", length = 20)
    private String merchantName;
}
