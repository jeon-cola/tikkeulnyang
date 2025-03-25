package com.c107.bucket.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "bucket_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BucketCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_category_id")
    private Integer bucketCategoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

}
