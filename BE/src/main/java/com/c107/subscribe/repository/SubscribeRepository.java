package com.c107.subscribe.repository;

import com.c107.subscribe.entity.SubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Integer> {

    List<SubscribeEntity> findByEmail(String email);
    // 결제일 순으로 모든 구독 정보 조회 (오름차순)
    List<SubscribeEntity> findByEmailOrderByPaymentDateAsc(String email);

    // 금액 순으로 모든 구독 정보 조회 (내림차순)
    List<SubscribeEntity> findByEmailOrderBySubscribePriceDesc(String email);

}