package com.c107.challenge.repository;

import com.c107.challenge.entity.ChallangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallangeRepository extends JpaRepository<ChallangeEntity, Integer> {
    List<ChallangeEntity> findByActiveFlag(Boolean activeFlag);
}