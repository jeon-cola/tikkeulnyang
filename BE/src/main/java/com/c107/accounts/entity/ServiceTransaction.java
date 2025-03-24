package com.c107.accounts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceTransaction {

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
    private Integer isWaste;

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
            this.isWaste = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
