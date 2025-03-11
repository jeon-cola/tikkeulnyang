package com.c107.subscribe.repository;

import com.c107.subscribe.entity.SubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Integer> {

    // 특정 사용자의 모든 구독 목록 조회
    List<SubscribeEntity> findByUserId(Integer userId);

    // 결제일 순으로 모든 구독 정보 조회 (오름차순)
    List<SubscribeEntity> findAllByOrderByPaymentDateAsc();

    // 금액 순으로 모든 구독 정보 조회 (내림차순)
    List<SubscribeEntity> findAllByOrderBySubscribePriceDesc();

}