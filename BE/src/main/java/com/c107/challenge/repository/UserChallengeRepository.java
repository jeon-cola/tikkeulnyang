package com.c107.challenge.repository;

import com.c107.challenge.entity.UserChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallengeEntity, Integer> {
    List<UserChallengeEntity> findByUserId(Integer userId);
    Optional<UserChallengeEntity> findByUserIdAndChallenge_ChallengeId(Integer userId, Integer challengeId);
    Optional<UserChallengeEntity> findByUserIdAndChallenge_ChallengeIdAndStatus(Integer userId, Integer challengeId, String status);

    List<UserChallengeEntity> findByChallenge_ChallengeIdAndStatus(Integer challengeId, String status);

    List<UserChallengeEntity> findByUserIdAndStatus(Integer userId, String status);

    List<UserChallengeEntity> findByUserIdAndStatusNot(Integer userId, String status);

}
