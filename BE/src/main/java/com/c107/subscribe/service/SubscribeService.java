package com.c107.subscribe.service;

import com.c107.subscribe.dto.SubscribeRequestDto;
import com.c107.subscribe.dto.SubscribeResponseDto;
import com.c107.subscribe.entity.SubscribeEntity;
import com.c107.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

    // 구독 정보 등록
//    @Transactional
//    public SubscribeEntity registerSubscribe(SubscribeRequestDto requestDto) {
//        SubscribeEntity subscribeEntity = requestDto.toEntity();
//        // 구독 정보 저장
//        return subscribeRepository.save(subscribeEntity);
//    }
    @Transactional
    public SubscribeEntity registerSubscribe(String email, SubscribeRequestDto requestDto) {
        SubscribeEntity subscribeEntity = SubscribeEntity.builder()
                .email(email)
                .subscribeName(requestDto.getSubscribeName())
                .subscribePrice(requestDto.getSubscribePrice())
                .paymentDate(requestDto.toEntity().getPaymentDate())
                .build();

        return subscribeRepository.save(subscribeEntity);
    }

    // 구독 정보 조회
//    @Transactional(readOnly = true)
//    public List<SubscribeEntity> getUserSubscribes(Integer userId) {
//        return subscribeRepository.findByUserId(userId);
//    }
    @Transactional(readOnly = true)
    public List<SubscribeEntity> getUserSubscribes(String email) {
        return subscribeRepository.findByEmail(email);
    }


    // 결제일 순으로 구독 리스트 조회
    @Transactional(readOnly = true)
    public List<SubscribeResponseDto> getSubscribesByPaymentDateOrder(String email) {
        List<SubscribeEntity> entities = subscribeRepository.findByEmailOrderByPaymentDateAsc(email);
        return entities.stream()
                .map(SubscribeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 금액 순으로 구독 리스트 조회(내림차순)
    @Transactional(readOnly = true)
    public List<SubscribeResponseDto> getSubscribesByPriceOrder(String email) {
        List<SubscribeEntity> entities = subscribeRepository.findByEmailOrderBySubscribePriceDesc(email);
        return entities.stream()
                .map(SubscribeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 구독 정보 삭제
    @Transactional
    public void deleteSubscribe(String email, Integer subscribeId) {
        SubscribeEntity subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("구독 정보를 찾을 수 없습니다."));

        if (!subscribe.getEmail().equals(email)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        subscribeRepository.delete(subscribe);
    }

//    @Transactional
//    public void deleteSubscribe(Integer subscribeId) {
//        subscribeRepository.deleteById(subscribeId);
//    }
}