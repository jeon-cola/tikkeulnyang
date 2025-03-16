package com.c107.paymenthistory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "region")
    private String region;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "cvc")
    private String cvc;
}