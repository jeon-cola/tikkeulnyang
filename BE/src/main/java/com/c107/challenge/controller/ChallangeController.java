package com.c107.challenge.controller;

import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallangeController {

    private final ChallengeService challengeService;

    // 🔹 챌린지 생성 (로그인한 유저 정보 자동 등록)
    @PostMapping
    public ResponseEntity<ChallengeResponseDto> createChallenge(@RequestBody CreateChallengeRequest request) {
        return ResponseEntity.ok(challengeService.createChallenge(request));
    }

    // 🔹 챌린지 삭제
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<String> deleteChallenge(@PathVariable Integer challengeId) {
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.ok("챌린지가 삭제되었습니다.");
    }

    // 🔹 챌린지 수정
    @PutMapping("/{challengeId}")
    public ResponseEntity<ChallengeResponseDto> updateChallenge(
            @PathVariable Integer challengeId,
            @RequestBody CreateChallengeRequest request) {
        return ResponseEntity.ok(challengeService.updateChallenge(challengeId, request));
    }

    // 🔹 공식 챌린지 조회 (페이지네이션 적용)
    @GetMapping("/official")
    public ResponseEntity<Page<ChallengeResponseDto>> getOfficialChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        return ResponseEntity.ok(challengeService.getOfficialChallenges(page, size));
    }
}
