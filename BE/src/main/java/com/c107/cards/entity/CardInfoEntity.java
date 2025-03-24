package com.c107.cards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "card_name", length = 20)
    private String cardName;

    @Column(name = "card_no", length = 16)
    private String cardNo;

    @Column(name = "cvc", length = 3)
    private String cvc;

    @Column(name = "card_type", length = 10)
    private String cardType;

    @Column(name = "region", length = 25)
    private String region;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}