package com.c107.share.controller;

import com.c107.common.util.ResponseUtil;
import com.c107.share.dto.InviteRequestDto;
import com.c107.share.dto.ShareCommentDto;
import com.c107.share.dto.ShareLedgerResponseDto;
import com.c107.share.dto.SharePartnerDto;
import com.c107.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
@Slf4j
public class ShareController {

    private final ShareService shareService;

    @Value("${app.base.url}")
    private String url;

    String token = UUID.randomUUID().toString();
    String invitationLink = url +"/"+ token;


    @GetMapping("/myledger")
    public ResponseEntity<?> getMyLedger(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        ShareLedgerResponseDto responseDto = shareService.getMyLedger(email, year, month);
        return ResponseUtil.success("소비 내역 조회에 성공했습니다.", responseDto);
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUser(@AuthenticationPrincipal String email) {
        String invitationLink = shareService.generateInvitationLink(email);
        return ResponseUtil.success("초대 링크 생성에 성공했습니다.", invitationLink);
    }


    // 초대 수락 엔드포인트
    @PostMapping("/accept/{token}")
    public ResponseEntity<?> acceptInvitation(
            @PathVariable String token,
            @AuthenticationPrincipal String invitedEmail
    ) {
        String message = shareService.acceptInvitation(token, invitedEmail);
        return ResponseUtil.success(message, null);
    }

    // 공유된 가계부 조회 엔드포인트 (권한 검증 포함)
    @GetMapping("/ledger/{token}")
    public ResponseEntity<?> getSharedLedger(
            @PathVariable String token,
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month // ✅ 이거 추가돼야 함
    ) {
        ShareLedgerResponseDto responseDto = shareService.getSharedLedger(token, email, year, month); // ✅ year, month 전달
        return ResponseUtil.success("공유 가계부 조회에 성공했습니다.", responseDto);
    }

    @GetMapping("/partners")
    public ResponseEntity<?> getMySharingPartners(@AuthenticationPrincipal String email) {
        List<SharePartnerDto> partners = shareService.getMyPartners(email);
        return ResponseUtil.success("공유 유저 조회 성공", partners);
    }

    @DeleteMapping("/unshare/{partnerUserId}")
    public ResponseEntity<?> unsharePartner(
            @AuthenticationPrincipal String myEmail,
            @PathVariable Long partnerUserId
    ) {
        shareService.unsharePartner(myEmail, partnerUserId);
        return ResponseUtil.success("공유 관계가 해제되었습니다.", null);
    }

    @GetMapping("/ledger/user/{targetUserId}")
    public ResponseEntity<?> getSharedLedgerByUserId(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal String myEmail,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        ShareLedgerResponseDto responseDto = shareService.getSharedLedgerByUserId(targetUserId, myEmail, year, month);
        return ResponseUtil.success("상대방 가계부 조회에 성공했습니다.", responseDto);
    }

    @GetMapping("/ledger/user/{targetUserId}/daily/{date}")
    public ResponseEntity<?> getPartnerDailyLedger(
            @PathVariable Long targetUserId,
            @PathVariable String date,
            @AuthenticationPrincipal String myEmail
    ) {
        Map<String, Object> dailyData = shareService.getPartnerDailyLedger(targetUserId, date, myEmail);
        return ResponseUtil.success("상대방 일별 소비 내역 조회 성공", dailyData);
    }

    // 특정 날짜의 댓글, 이모지 조회
    @GetMapping("/comments/{date}")
    public ResponseEntity<?> getComments(
            @PathVariable String date,
            @AuthenticationPrincipal String myEmail
    ) {
        ShareCommentDto.DailyCommentsResponse comments = shareService.getDailyComments(date, myEmail);
        return ResponseUtil.success("댓글 및 이모지 조회 성공", comments);
    }

    // 타인 평가 이모지, 댓글 등록
    @PostMapping("/comments/{targetUserId}/{date}")
    public ResponseEntity<?> addComment(
            @PathVariable Long targetUserId,
            @PathVariable String date,
            @RequestBody ShareCommentDto.CommentRequest request,
            @AuthenticationPrincipal String myEmail
    ) {
        ShareCommentDto.CommentResponse comment = shareService.addComment(targetUserId, date, request, myEmail);
        return ResponseUtil.success("댓글 및 이모지 등록 성공", comment);
    }

}