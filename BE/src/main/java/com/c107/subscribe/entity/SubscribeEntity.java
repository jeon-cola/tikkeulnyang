package com.c107.subscribe.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribe")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Integer subscribeId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "subscribe_name", length = 20)
    private String subscribeName;

    @Column(name = "subscribe_price")
    private Integer subscribePrice;

    @Column(name = "payment_date")
    private Integer paymentDate;

}