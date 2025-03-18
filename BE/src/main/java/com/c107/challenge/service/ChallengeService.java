package com.c107.challenge.service;

import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.entity.ChallengeEntity;
import com.c107.challenge.repository.ChallengeRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    // 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @Transactional
    public ChallengeResponseDto createChallenge(CreateChallengeRequest request) {
        String createdBy = getAuthenticatedUserEmail();
        boolean isAdmin = isAdmin();

        ChallengeEntity entity = ChallengeEntity.builder()
                .challengeName(request.getChallengeName())
                .challengeType(request.getChallengeType())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .createdBy(createdBy)
                .minRequired(1) // 기본 최소 참여 인원 설정
                .publicFlag(true) // 기본 공개 설정
                .activeFlag(true) // 활성화된 챌린지
                .challengeCategory(isAdmin ? "공식챌린지" : "유저챌린지") // 운영자는 공식, 유저는 유저 챌린지
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .currentParticipants(0)
                .deleted(false) // 기본값 false (삭제되지 않음)
                .build();

        ChallengeEntity saved = challengeRepository.save(entity);
        return mapToDto(saved);
    }

    // 챌린지 삭제 (deleted 값 변경)
    @Transactional
    public void deleteChallenge(Integer challengeId) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        String loggedInUser = getAuthenticatedUserEmail();
        boolean isAdmin = isAdmin();

        if (!isAdmin && !challenge.getCreatedBy().equals(loggedInUser)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "삭제 권한이 없습니다.");
        }

        challenge.setDeleted(true); // DB에서 실제 삭제하지 않고 삭제 플래그만 변경
        challengeRepository.save(challenge);
    }

    // 챌린지 수정 (본인만 가능, 운영자는 수정 불가)
    @Transactional
    public ChallengeResponseDto updateChallenge(Integer challengeId, CreateChallengeRequest request) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        String loggedInUser = getAuthenticatedUserEmail();

        if (isAdmin()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "운영자는 챌린지를 수정할 수 없습니다.");
        }

        if (!challenge.getCreatedBy().equals(loggedInUser)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "수정 권한이 없습니다.");
        }

        challenge.setChallengeName(request.getChallengeName());
        challenge.setChallengeType(request.getChallengeType());
        challenge.setTargetAmount(request.getTargetAmount());
        challenge.setStartDate(request.getStartDate());
        challenge.setEndDate(request.getEndDate());
        challenge.setDescription(request.getDescription());
        challenge.setUpdatedAt(LocalDateTime.now());

        ChallengeEntity updated = challengeRepository.save(challenge);
        return mapToDto(updated);
    }

    // 공식 챌린지 조회 (페이지네이션 적용)
    public Page<ChallengeResponseDto> getOfficialChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository.findByChallengeCategoryAndDeleted("공식챌린지", false, PageRequest.of(page, size));
        return challenges.map(this::mapToDto);
    }

    // 유저 챌린지 조회 (페이지네이션 적용)
    public Page<ChallengeResponseDto> getUserChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository.findByChallengeCategoryAndDeleted("유저챌린지", false, PageRequest.of(page, size));
        return challenges.map(this::mapToDto);
    }

    // 현재 로그인한 유저 이메일 가져오기
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // 현재 로그인한 유저가 운영자인지 확인
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // 챌린지 조회 유틸 메서드
    private ChallengeEntity findChallengeById(Integer challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "챌린지를 찾을 수 없습니다."));
    }

    // Entity -> DTO 변환
    private ChallengeResponseDto mapToDto(ChallengeEntity entity) {
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
                .challengeCategory(entity.getChallengeCategory())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
