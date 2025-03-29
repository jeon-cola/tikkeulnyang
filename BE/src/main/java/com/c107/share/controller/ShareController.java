package com.c107.share.controller;

import com.c107.common.util.ResponseUtil;
import com.c107.share.dto.ShareLedgerResponseDto;
import com.c107.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
@Slf4j
public class ShareController {

    private final ShareService shareService;

    @GetMapping("/myledger")
    public ResponseEntity<?> getMyLedger(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        ShareLedgerResponseDto responseDto = shareService.getMyLedger(email, year, month);
        return ResponseUtil.success("소비 내역 조회에 성공했습니다.", responseDto);
    }
}