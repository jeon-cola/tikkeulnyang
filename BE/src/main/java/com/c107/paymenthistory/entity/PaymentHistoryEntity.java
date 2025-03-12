package com.c107.paymenthistory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "payment_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_history_id")
    private Integer paymentHistoryId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "transaction_time")
    private String transactionTime;

    @Column(name = "transaction_type")
    private String transactionType; // "EXPENSE"(지출) 또는 "INCOME"(수입)

    @Column(name = "transaction_balance")
    private String transactionBalance;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "card_status")
    private String cardStatus;

    @Column(name = "is_waste")
    private Boolean isWaste;
}