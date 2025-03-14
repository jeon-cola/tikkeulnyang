package com.c107.challenge.controller;

import com.c107.challenge.dto.ChallengeResponseDto;
import com.c107.challenge.dto.CreateChallengeRequest;
import com.c107.challenge.dto.DepositChallengeRequest;
import com.c107.challenge.dto.DistributeChallengeRequest;
import com.c107.challenge.service.ChallangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallangeController {

    private final ChallangeService challangeService;

    // 1. 챌린지 생성
    @PostMapping
    public ResponseEntity<ChallengeResponseDto> createChallenge(@RequestBody CreateChallengeRequest request) {
        ChallengeResponseDto response = challangeService.createChallenge(request);
        return ResponseEntity.ok(response);
    }

    // 2. 챌린지 삭제
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<String> deleteChallenge(@PathVariable Integer challengeId) {
        challangeService.deleteChallenge(challengeId);
        return ResponseEntity.ok("Challenge deleted successfully");
    }

    // 3. 노지출 챌린지 생성
    @PostMapping("/no-spend")
    public ResponseEntity<ChallengeResponseDto> createNoSpendChallenge(@RequestBody CreateChallengeRequest request) {
        ChallengeResponseDto response = challangeService.createNoSpendChallenge(request);
        return ResponseEntity.ok(response);
    }

    // 4. 챌린지 비용 입금
    @PostMapping("/deposit")
    public ResponseEntity<String> depositChallengeCost(@RequestBody DepositChallengeRequest request) {
        challangeService.depositChallengeCost(request);
        return ResponseEntity.ok("Challenge deposit processed successfully");
    }

    // 5. 챌린지 성공 시 비용 분배
    @PostMapping("/distribute")
    public ResponseEntity<String> distributeChallengeReward(@RequestBody DistributeChallengeRequest request) {
        challangeService.distributeChallengeReward(request);
        return ResponseEntity.ok("Challenge reward distributed successfully");
    }

    // 6. 진행중인 챌린지 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<ChallengeResponseDto>> getChallengeList() {
        List<ChallengeResponseDto> challenges = challangeService.getChallengeList();
        return ResponseEntity.ok(challenges);
    }

    // 7. 참여중인 챌린지 상세 조회
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeResponseDto> getChallengeDetail(@PathVariable Integer challengeId) {
        ChallengeResponseDto challenge = challangeService.getChallengeDetail(challengeId);
        return ResponseEntity.ok(challenge);
    }

    // 8. 과거 참여했던 챌린지 이력 조회
    @GetMapping("/history")
    public ResponseEntity<List<ChallengeResponseDto>> getPastChallengesHistory(@RequestParam Integer userId) {
        List<ChallengeResponseDto> history = challangeService.getPastChallengesHistory(userId);
        return ResponseEntity.ok(history);
    }
}
