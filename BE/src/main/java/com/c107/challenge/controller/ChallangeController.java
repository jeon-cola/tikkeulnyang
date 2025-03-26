package com.c107.challenge.controller;

import com.c107.challenge.dto.ChallengeDetailResponseDto;
import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.dto.PastChallengeResponseDto;
import com.c107.challenge.service.ChallengeService;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallangeController {

    private final ChallengeService challengeService;

    // 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @PostMapping
    public ResponseEntity<ChallengeResponseDto> createChallenge(@RequestBody CreateChallengeRequest request) {
        if (request.getStartDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지는 다음 날짜부터 시작할 수 있습니다.");
        }
        return ResponseEntity.ok(challengeService.createChallenge(request));
    }

    // 챌린지 삭제
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<String> deleteChallenge(@PathVariable Integer challengeId) {
        ChallengeResponseDto challenge = challengeService.getChallengeById(challengeId);
        if (challenge.getActiveFlag()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "시작된 챌린지는 삭제할 수 없습니다.");
        }
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.ok("챌린지가 삭제되었습니다.");
    }

    // 챌린지 수정 (항상 예외 발생)
    @PutMapping("/{challengeId}")
    public ResponseEntity<ChallengeResponseDto> updateChallenge(
            @PathVariable Integer challengeId,
            @RequestBody CreateChallengeRequest request) {
        return ResponseEntity.ok(challengeService.updateChallenge(challengeId, request));
    }

    // 공식 챌린지 조회 (페이지네이션 적용)
    @GetMapping("/official")
    public ResponseEntity<Page<ChallengeResponseDto>> getOfficialChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        return ResponseEntity.ok(challengeService.getOfficialChallenges(page, size));
    }

    // 유저 챌린지 조회 (페이지네이션 적용)
    @GetMapping("/user")
    public ResponseEntity<Page<ChallengeResponseDto>> getUserChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        return ResponseEntity.ok(challengeService.getUserChallenges(page, size));
    }

    // 챌린지 참여 엔드포인트 (로그인한 유저 자동 적용)
    @PostMapping("/{challengeId}/join")
    public ResponseEntity<String> joinChallenge(@PathVariable Integer challengeId) {
        challengeService.joinChallenge(challengeId);
        return ResponseEntity.ok("챌린지 참여가 완료되었습니다.");
    }

    // 챌린지 참여 취소 엔드포인트 (로그인한 유저 자동 적용)
    @PostMapping("/{challengeId}/cancel")
    public ResponseEntity<String> cancelChallengeParticipation(@PathVariable Integer challengeId) {
        challengeService.cancelChallengeParticipation(challengeId);
        return ResponseEntity.ok("챌린지 참여 취소가 완료되었습니다.");
    }

    @GetMapping("/participated")
    public ResponseEntity<List<ChallengeResponseDto>> getParticipatedChallenges() {
        List<ChallengeResponseDto> participatedChallenges = challengeService.getParticipatedChallenges();
        return ResponseEntity.ok(participatedChallenges);
    }

    // 챌린지 상세조회 엔드포인트 (기존 정보 + 참가자수, 성공률 구간별 분포, 평균 성공률)
    @GetMapping("/{challengeId}/detail")
    public ResponseEntity<ChallengeDetailResponseDto> getChallengeDetail(@PathVariable Integer challengeId) {
        ChallengeDetailResponseDto detail = challengeService.getChallengeDetail(challengeId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/past")
    public ResponseEntity<List<PastChallengeResponseDto>> getPastParticipatedChallenges() {
        List<PastChallengeResponseDto> pastChallenges = challengeService.getPastParticipatedChallenges();
        return ResponseEntity.ok(pastChallenges);
    }

    // 챌린지 종료 후 환불 정산 엔드포인트
    @PostMapping("/{challengeId}/settle")
    public ResponseEntity<String> settleChallenge(@PathVariable Integer challengeId) {
        challengeService.settleChallenge(challengeId);
        return ResponseEntity.ok("챌린지 환불 정산이 완료되었습니다.");
    }

}
