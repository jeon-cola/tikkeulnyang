package com.c107.share.repository;

import com.c107.share.entity.ShareNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShareNotificationRepository extends JpaRepository<ShareNotificationEntity, Integer> {

    // 월별 알림 날짜 조회 (네이티브 쿼리 사용)
    @Query(value = "SELECT DISTINCT DATE_FORMAT(target_date, '%Y-%m-%d') AS formatted_date " +
            "FROM share_notification " +
            "WHERE user_id = :userId AND is_read = 0 " +
            "AND YEAR(target_date) = :year " +
            "AND MONTH(target_date) = :month " +
            "ORDER BY formatted_date",
            nativeQuery = true)
    List<String> findDistinctNotificationDatesByUserIdAndYearAndMonth(
            Integer userId, Integer year, Integer month);

    // 특정 사용자의 특정 날짜 알림을 모두 읽음 처리
    @Modifying
    @Query("UPDATE ShareNotificationEntity n SET n.isRead = 1, n.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE n.userId = :userId AND n.targetDate = :targetDate AND n.isRead = 0")
    int markAllNotificationsAsReadByDate(Integer userId, LocalDate targetDate);
}