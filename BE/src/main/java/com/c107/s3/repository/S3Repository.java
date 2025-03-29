package com.c107.s3.repository;

import com.c107.s3.entity.S3Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface S3Repository extends JpaRepository<S3Entity, Integer> {
    // 예를 들어, 특정 유저의 프로필 이미지를 조회할 때
    Optional<S3Entity> findTopByUsageTypeAndUsageIdOrderByCreatedAtDesc(String usageType, Integer usageId);
}
