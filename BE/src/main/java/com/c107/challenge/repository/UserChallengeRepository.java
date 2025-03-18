package com.c107.challenge.repository;

import com.c107.challenge.entity.UserChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserChallengeRepository extends JpaRepository<UserChallengeEntity, Integer> {
    List<UserChallengeEntity> findByUserId(Integer userId);
}
