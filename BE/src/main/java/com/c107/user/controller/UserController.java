package com.c107.user.controller;

import com.c107.common.util.JwtUtil;
import com.c107.common.util.ResponseUtil;
import com.c107.s3.service.S3Service;
import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import com.c107.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final S3Service s3Service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        Optional<User> existingUser = userRepository.findByNickname(nickname);
        if (existingUser.isPresent()) {
            return ResponseUtil.badRequest("사용중인 닉네임입니다.", null);
        } else {
            return ResponseUtil.success("닉네임 사용 가능", null);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "accessToken", required = false) String accessToken,
                                            HttpServletResponse response) {
        if (accessToken == null) {
            return ResponseUtil.badRequest("Access 토큰 쿠키가 존재하지 않습니다.", null);
        }

        String email;
        try {
            email = jwtUtil.parseClaims(accessToken).getSubject();
        } catch (Exception e) {
            return ResponseUtil.badRequest("토큰 검증에 실패하였습니다.", null);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseUtil.badRequest("사용자 정보를 찾을 수 없습니다.", null);
        }

        User user = userOpt.get();
        // 프로필 이미지 URL을 S3 이미지 테이블에서 조회 (예: usageType "PROFILE")
        String profileImageUrl = userService.getProfileImageUrl(user.getUserId());

        Map<String, Object> responseBody = Map.of(
                "id", user.getUserId(),
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "role", user.getRole(),
                "deposit", user.getDeposit(),
                "profileImage", profileImageUrl
        );
        return ResponseEntity.ok(ResponseUtil.success("현재 사용자 정보", responseBody));
    }

    // 유저 프로필 이미지 업로드 엔드포인트
    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                @RequestParam("file") MultipartFile file) {
        if (accessToken == null) {
            return ResponseUtil.badRequest("Access 토큰 쿠키가 존재하지 않습니다.", null);
        }
        String email;
        try {
            email = jwtUtil.parseClaims(accessToken).getSubject();
        } catch (Exception e) {
            return ResponseUtil.badRequest("토큰 검증에 실패하였습니다.", null);
        }
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseUtil.badRequest("사용자 정보를 찾을 수 없습니다.", null);
        }
        User user = userOpt.get();
        try {
            // S3ImageService를 호출하여 파일 업로드 및 이미지 DB 저장
            String fileUrl = s3Service.uploadProfileImage(file, "PROFILE", user.getUserId());
            // 프로필 이미지 변경 후, 추가 로직(예: 기존 이미지 삭제 등)을 처리할 수 있습니다.
            return ResponseUtil.success("프로필 이미지 업로드 성공", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.badRequest("파일 업로드 실패", null);
        }
    }
}
