package com.c107.accounts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_transactions", schema = "catcat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_transaction_id")
    private Integer serviceTransactionId;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_type", length = 10)
    private String transactionType;

    @Column(name = "transaction_account_no", length = 16)
    private String transactionAccountNo;

    @Column(name = "transaction_balance")
    private Integer transactionBalance;

    @Column(name = "transaction_after_balance")
    private Integer transactionAfterBalance;

    @Column(name = "description", length = 50)
    private String description;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
