package com.c107.bucket.repository;

import com.c107.bucket.entity.BucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BucketRepository extends JpaRepository<BucketEntity, Integer> {
    List<BucketEntity> findByUserId(Integer userId);
    Optional<BucketEntity> findByBucketIdAndUserId(Integer bucketId, Integer userId);
    Optional<BucketEntity> findTopByUserIdOrderByCreatedAtDesc(Integer userId);

}
