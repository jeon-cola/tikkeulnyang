package com.c107.bucket.repository;

import com.c107.bucket.entity.BucketCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BucketCategoryRepository extends JpaRepository<BucketCategoryEntity, Integer> {
    Optional<BucketCategoryEntity> findByCategoryName(String categoryName);
}
