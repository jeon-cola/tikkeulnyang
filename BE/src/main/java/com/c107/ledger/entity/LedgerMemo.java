package com.c107.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_memos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LedgerMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Integer memoId;

    @Column(name = "memo_date", nullable = false)
    private LocalDate memoDate;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
