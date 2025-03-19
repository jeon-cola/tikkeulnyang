package com.c107.challenge.repository;

import com.c107.challenge.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Integer> {
    Page<ChallengeEntity> findByChallengeTypeAndDeleted(String challengeType, boolean deleted, Pageable pageable);
    Optional<ChallengeEntity> findByChallengeIdAndDeletedFalse(Integer challengeId);
    // ✅ 시작 날짜가 오늘 이전이면서 activeFlag가 false인 챌린지 찾기
    List<ChallengeEntity> findByStartDateBeforeAndActiveFlagFalse(LocalDate today);
}
