package com.c107.accounts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_transaction_id")
    private Integer accountTransactionId;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "category", length = 20)
    private String category;

    @Column(name = "transaction_type", nullable = false, length = 10)
    private String transactionType; // 예: DEPOSIT, WITHDRAW, CHALLENGE_JOIN, CHALLENGE_REWARD 등

    @Column(name = "transaction_account_no", length = 16)
    private String transactionAccountNo;

    @Column(name = "transaction_balance", nullable = false)
    private Integer transactionBalance;

    @Column(name = "transaction_after_balance", nullable = false)
    private Integer transactionAfterBalance;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_waste", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private Boolean isWaste;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if(this.isWaste == null) {
            this.isWaste = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
