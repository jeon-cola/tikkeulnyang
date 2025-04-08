package com.c107.challenge.repository;

import com.c107.challenge.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Integer> {
    // 기존 메서드들
    Page<ChallengeEntity> findByChallengeTypeAndDeleted(String challengeType, boolean deleted, Pageable pageable);
    Optional<ChallengeEntity> findByChallengeIdAndDeletedFalse(Integer challengeId);
    List<ChallengeEntity> findByStartDateBeforeAndActiveFlagFalse(LocalDate today);
    Page<ChallengeEntity> findByDeletedFalse(Pageable pageable);
    List<ChallengeEntity> findByEndDateBefore(LocalDate now);
    List<ChallengeEntity> findByEndDateBeforeAndDeletedFalseAndActiveFlagTrue(LocalDate now);

    // soft delete되지 않고(active_flag가 false) 종료일이 오늘 이후(또는 오늘 포함)인 챌린지 조회
    Page<ChallengeEntity> findByChallengeTypeAndDeletedFalseAndActiveFlagFalseAndEndDateGreaterThanEqual(
            String challengeType, LocalDate currentDate, Pageable pageable);
}
