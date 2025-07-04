package com.c107.share.repository;

import com.c107.share.entity.ShareEntity;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<ShareEntity, Integer> {

    Optional<ShareEntity> findByInvitationLinkEndingWith(String token);
    @Query("SELECT s FROM ShareEntity s WHERE (s.ownerId = :userId OR s.sharedUserId = :userId) AND s.status = 1")
    List<ShareEntity> findByUserInActiveShare(@Param("userId") Integer userId);

    @Query("SELECT s FROM ShareEntity s " +
            "WHERE ((s.ownerId = :user1 AND s.sharedUserId = :user2) " +
            "   OR (s.ownerId = :user2 AND s.sharedUserId = :user1)) " +
            "AND s.status = 1")
    Optional<ShareEntity> findActiveShareByUsers(@Param("user1") Integer user1, @Param("user2") Integer user2);

    @Transactional
    int deleteByStatusAndLinkExpireBefore(Integer status, LocalDateTime time);

    @Query("""
   SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
   FROM ShareEntity s
   WHERE 
     (
       (s.ownerId = :userA AND s.sharedUserId = :userB) 
       OR (s.ownerId = :userB AND s.sharedUserId = :userA)
     )
     AND s.status = 1
""")
    boolean existsActiveShareBetween(Integer userA, Integer userB);

}

