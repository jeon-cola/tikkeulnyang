package com.c107.challenge.controller;

import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.service.ChallengeService;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallangeController {

    private final ChallengeService challengeService;

    // 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @PostMapping
    public ResponseEntity<ChallengeResponseDto> createChallenge(@RequestBody CreateChallengeRequest request) {
        if (request.getStartDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "챌린지는 최소 24시간 이후부터 시작할 수 있습니다.");
        }
        return ResponseEntity.ok(challengeService.createChallenge(request));
    }

    // 챌린지 삭제
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<String> deleteChallenge(@PathVariable Integer challengeId) {
        ChallengeResponseDto challenge = challengeService.getChallengeById(challengeId);

        // 🔹 활성화된 챌린지는 삭제할 수 없음
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
}
