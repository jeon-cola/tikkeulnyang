package com.c107.budget.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "spending_amount")
    private Integer spendingAmount;

    @Column(name = "remaining_amount")
    private Integer remainingAmount;

    @Column(name = "is_exceed")
    private Boolean isExceed;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}