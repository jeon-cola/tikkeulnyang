package com.c107.challenge.service;

import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.entity.ChallengeEntity;
import com.c107.challenge.repository.ChallengeRepository;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    private final ChallengeRepository challengeRepository;

    // 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @Transactional
    public ChallengeResponseDto createChallenge(CreateChallengeRequest request) {
        String createdBy = getAuthenticatedUserEmail();
        boolean isAdmin = isAdmin();

        // 현재 시간 기준으로 최소 시작 가능 날짜 계산 (내일 00:00부터)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestStartTime = now.toLocalDate().plusDays(1).atStartOfDay(); // 내일 00:00

        // 요청한 시작 날짜가 최소 시작 가능 날짜보다 빠르면 예외 발생
        if (request.getStartDate().isBefore(earliestStartTime.toLocalDate())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지는 등록 후 최소 하루 뒤 자정(00:00)부터 시작할 수 있습니다.");
        }

        ChallengeEntity entity = ChallengeEntity.builder()
                .challengeName(request.getChallengeName())
                .challengeType(isAdmin ? "공식챌린지" : "유저챌린지") // 운영자는 공식, 유저는 유저 챌린지
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .createdBy(createdBy)
                .maxParticipants(request.getMaxParticipants()) // 최대 인원 설정
                .limitAmount(request.getLimitAmount())
                .publicFlag(true) // 기본 공개 설정
                .activeFlag(false) // 기본적으로 비활성화 (시작 시간이 되면 활성화)
                .challengeCategory(request.getChallengeCategory())
                .createdAt(now)
                .updatedAt(now)
                .currentParticipants(1)
                .deleted(false)
                .build();

        ChallengeEntity saved = challengeRepository.save(entity);
        return mapToDto(saved);
    }


    // 챌린지 삭제 (시작 전인 경우만 삭제 가능)
    @Transactional
    public void deleteChallenge(Integer challengeId) {
        ChallengeEntity challenge = findChallengeById(challengeId);
        String loggedInUser = getAuthenticatedUserEmail();
        boolean isAdmin = isAdmin();

        // 삭제 가능 조건: 챌린지가 아직 시작되지 않은 경우 (activeFlag가 false)와 시작일이 미래여야 함.
        if (challenge.getActiveFlag() || !LocalDate.now().isBefore(challenge.getStartDate())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "시작된 챌린지는 삭제할 수 없습니다.");
        }

        // 삭제 권한 확인: 운영자 또는 생성자만 가능
        if (!isAdmin && !challenge.getCreatedBy().equals(loggedInUser)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "삭제 권한이 없습니다.");
        }

        challenge.setDeleted(true);
        challengeRepository.save(challenge);
    }

    // 챌린지 수정은 어떠한 경우에도 불가능하도록 처리
    @Transactional
    public ChallengeResponseDto updateChallenge(Integer challengeId, CreateChallengeRequest request) {
        throw new CustomException(ErrorCode.UNAUTHORIZED, "챌린지는 수정할 수 없습니다.");
    }

    // 공식 챌린지 조회 (페이지네이션 적용)
    public Page<ChallengeResponseDto> getOfficialChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository.findByChallengeTypeAndDeleted("공식챌린지", false, PageRequest.of(page, size));
        return challenges.map(this::mapToDto);
    }

    // 유저 챌린지 조회 (페이지네이션 적용)
    public Page<ChallengeResponseDto> getUserChallenges(int page, int size) {
        Page<ChallengeEntity> challenges = challengeRepository.findByChallengeTypeAndDeleted("유저챌린지", false, PageRequest.of(page, size));
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

    // 챌린지 ID로 조회하는 메서드 추가
    public ChallengeResponseDto getChallengeById(Integer challengeId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "챌린지를 찾을 수 없습니다."));
        return mapToDto(challenge);
    }

    // ✅ 서버가 켜질 때 실행 (이전 날짜 챌린지도 활성화)
    @PostConstruct
    public void initializeChallenges() {
        activatePendingChallenges();
        logger.info("✅ 서버 시작 시, 활성화되지 않은 챌린지 확인 완료!");
    }

    // ✅ 매일 0시 0분에 실행 (정상적으로 실행되는 경우)
    @Scheduled(cron = "0 0 0 * * *")
    public void activateChallenges() {
        activatePendingChallenges();
        logger.info("✅ 0시 0분에 챌린지 활성화 완료!");
    }

    // ✅ 활성화되지 않은 챌린지를 찾고 활성화하는 공통 로직
    private void activatePendingChallenges() {
        LocalDate today = LocalDate.now();
        List<ChallengeEntity> challenges = challengeRepository.findByStartDateBeforeAndActiveFlagFalse(today);

        for (ChallengeEntity challenge : challenges) {
            challenge.setActiveFlag(true);
            challengeRepository.save(challenge);
            logger.debug("🔹 챌린지 활성화됨: ID = {}, Name = {}", challenge.getChallengeId(), challenge.getChallengeName());
        }
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
                .maxParticipants(entity.getMaxParticipants())
                .activeFlag(entity.getActiveFlag())
                .challengeCategory(entity.getChallengeCategory())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
