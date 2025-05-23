package com.c107.subscribe.service;

import com.c107.subscribe.dto.SubscribeRequestDto;
import com.c107.subscribe.dto.SubscribeResponseDto;
import com.c107.subscribe.entity.SubscribeEntity;
import com.c107.subscribe.repository.SubscribeRepository;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;

    @Caching(evict = {
            @CacheEvict(value = "subscribeListCache", key = "#email"),
            @CacheEvict(value = "subscribeListCache", key = "#email + ':paymentDate'"),
            @CacheEvict(value = "subscribeListCache", key = "#email + ':priceDesc'")
    })
    @Transactional
    public SubscribeEntity registerSubscribe(String email, SubscribeRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Integer paymentDay;
        try {
            paymentDay = Integer.parseInt(requestDto.getPaymentDate().split("-")[2]);
        } catch (Exception e) {
            paymentDay = 1;
        }
        SubscribeEntity subscribeEntity = SubscribeEntity.builder()
                .userId(user.getUserId())
                .email(email)
                .subscribeName(requestDto.getSubscribeName())
                .subscribePrice(requestDto.getSubscribePrice())
                .paymentDate(paymentDay)
                .build();

        return subscribeRepository.save(subscribeEntity);
    }

    @Cacheable(value = "subscribeListCache", key = "#email")
    @Transactional(readOnly = true)
    public List<SubscribeResponseDto> getUserSubscribes(String email) {
        return subscribeRepository.findByEmail(email).stream()
                .map(SubscribeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "subscribeListCache", key = "#email + ':paymentDate'")
    @Transactional(readOnly = true)
    public List<SubscribeResponseDto> getSubscribesByPaymentDateOrder(String email) {
        List<SubscribeEntity> entities = subscribeRepository.findByEmailOrderByPaymentDateAsc(email);
        return entities.stream()
                .map(SubscribeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "subscribeListCache", key = "#email + ':priceDesc'")
    @Transactional(readOnly = true)
    public List<SubscribeResponseDto> getSubscribesByPriceOrder(String email) {
        List<SubscribeEntity> entities = subscribeRepository.findByEmailOrderBySubscribePriceDesc(email);
        return entities.stream()
                .map(SubscribeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "subscribeListCache", key = "#email"),
            @CacheEvict(value = "subscribeListCache", key = "#email + ':paymentDate'"),
            @CacheEvict(value = "subscribeListCache", key = "#email + ':priceDesc'")
    })
    @Transactional
    public void deleteSubscribe(String email, Integer subscribeId) {
        SubscribeEntity subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("구독 정보를 찾을 수 없습니다."));

        if (!subscribe.getEmail().equals(email)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        subscribeRepository.delete(subscribe);
    }
}
