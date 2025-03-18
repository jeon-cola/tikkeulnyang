package com.c107.challenge.repository;

import com.c107.challenge.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Integer> {
    Page<ChallengeEntity> findByChallengeCategoryAndDeleted(String challengeCategory, boolean deleted, Pageable pageable);
    Optional<ChallengeEntity> findByChallengeIdAndDeletedFalse(Integer challengeId);

}
