package com.c107.bucket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "buckets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BucketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_id")
    private Integer bucketId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "bucket_category_id")
    private Integer bucketCategoryId;

    @Column(name = "title")
    private String title;

    @Column(name = "saving_amount")
    private Integer savingAmount;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "saving_days")
    private String savingDays;

    @Column(name = "saving_account")
    private String savingAccount;

    @Column(name = "withdrawal_account")
    private String withdrawalAccount;

    @Column(name = "description")
    private String description;

    @Column(name = "count")
    private Integer count;

    @Column(name = "saved_amount")
    private Integer savedAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}