package com.c107.challenge.service;

import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.dto.DepositChallengeRequest;
import com.c107.challenge.dto.DistributeChallengeRequest;
import com.c107.challenge.entity.ChallangeEntity;
import com.c107.challenge.repository.ChallangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallangeService {

    private final ChallangeRepository challangeRepository;

    // 1. 챌린지 생성
    @Transactional
    public ChallengeResponseDto createChallenge(CreateChallengeRequest request) {
        ChallangeEntity entity = ChallangeEntity.builder()
                .challengeName(request.getChallengeName())
                .challengeType(request.getChallengeType())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .minRequired(request.getMinRequired())
                .publicFlag(request.getPublicFlag())
                .activeFlag(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .currentParticipants(0)
                .build();

        ChallangeEntity saved = challangeRepository.save(entity);
        return mapToDto(saved);
    }

    // 2. 챌린지 삭제
    @Transactional
    public void deleteChallenge(Integer challengeId) {
        challangeRepository.deleteById(challengeId);
    }

    // 3. 노지출 챌린지 생성 (특별 로직 적용)
    @Transactional
    public ChallengeResponseDto createNoSpendChallenge(CreateChallengeRequest request) {
        // 노지출 챌린지의 경우 challengeType을 "NO_SPEND"로 강제 설정
        request.setChallengeType("NO_SPEND");
        return createChallenge(request);
    }

    // 4. 챌린지 비용 입금 처리
    @Transactional
    public void depositChallengeCost(DepositChallengeRequest request) {
        // 실제 입금 처리는 연관 테이블(예: user_challenges) 업데이트 필요
        // 예시로 로그를 남김
        System.out.println("Deposit received for challengeId " + request.getChallengeId() +
                " from user " + request.getUserId() +
                " amount: " + request.getDepositAmount());
    }

    // 5. 챌린지 성공 시 비용 분배 처리
    @Transactional
    public void distributeChallengeReward(DistributeChallengeRequest request) {
        // 실제 분배 로직을 구현
        System.out.println("Distributing rewards for challengeId " + request.getChallengeId());
    }

    // 6. 진행중인 챌린지 리스트 조회 (activeFlag true)
    public List<ChallengeResponseDto> getChallengeList() {
        List<ChallangeEntity> challenges = challangeRepository.findByActiveFlag(true);
        return challenges.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // 7. 참여중인 챌린지 상세 조회
    public ChallengeResponseDto getChallengeDetail(Integer challengeId) {
        ChallangeEntity entity = challangeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));
        return mapToDto(entity);
    }

    // 8. 과거 참여했던 챌린지 이력 조회
    public List<ChallengeResponseDto> getPastChallengesHistory(Integer userId) {
        // 실제 구현 시 user_challenges 테이블과 join하여 userId 기준으로 조회
        // 예제에서는 activeFlag가 false인 챌린지를 과거 이력으로 간주
        List<ChallangeEntity> challenges = challangeRepository.findAll().stream()
                .filter(ch -> Boolean.FALSE.equals(ch.getActiveFlag()))
                .collect(Collectors.toList());
        return challenges.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Entity -> DTO 변환
    private ChallengeResponseDto mapToDto(ChallangeEntity entity) {
        return ChallengeResponseDto.builder()
                .challengeId(entity.getChallengeId())
                .challengeName(entity.getChallengeName())
                .challengeType(entity.getChallengeType())
                .targetAmount(entity.getTargetAmount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .activeFlag(entity.getActiveFlag())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
