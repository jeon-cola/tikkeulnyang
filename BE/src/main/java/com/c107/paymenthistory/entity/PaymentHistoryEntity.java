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

    @Column(name = "transcation_time")
    private String transcationTime;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_balance")
    private String transactionBalance;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "card_status")
    private String cardStatus;

    @Column(name = "is_waste")
    private Boolean isWaste;

    @Column(name = "transaction_unique_no")
    private String transactionUniqueNo;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_issuer_code")
    private String cardIssuerCode;

    @Column(name = "card_issuer_name")
    private String cardIssuerName;

    @Column(name = "bill_statements_yn")
    private String billStatementsYn;

    @Column(name = "bill_statements_status")
    private String billStatementsStatus;
}