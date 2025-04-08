package com.c107.share.repository;

import com.c107.share.entity.ShareInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShareInteractionRepository extends JpaRepository<ShareInteractionEntity, Integer> {

    // 특정 공유ID에 대한 특정 날짜의 모든 상호작용 조회
    List<ShareInteractionEntity> findByShareIdAndTargetDateOrderByCreatedAtDesc(Integer shareId, LocalDate targetDate);

    // 특정 공유 ID와 날짜에 대한 특정 이모지 개수 조회
    @Query("SELECT COUNT(i) FROM ShareInteractionEntity i WHERE i.shareId = :shareId AND i.targetDate = :targetDate AND i.emoji = :emoji")
    Integer countByShareIdAndTargetDateAndEmoji(Integer shareId, LocalDate targetDate, Integer emoji);

    // 특정 사용자가 특정 공유 관계의 특정 날짜에 이미 상호작용했는지 확인
    boolean existsByUserIdAndShareIdAndTargetDate(Integer userId, Integer shareId, LocalDate targetDate);
}