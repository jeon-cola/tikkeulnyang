package com.c107.user.controller;

import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.common.util.ResponseUtil;
import com.c107.s3.service.S3Service;
import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.dto.UserUpdateRequestDto;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import com.c107.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        Optional<User> existingUser = userRepository.findByNickname(nickname);
        if (existingUser.isPresent()) {
            return ResponseUtil.badRequest("사용중인 닉네임입니다.", null);
        } else {
            return ResponseUtil.success("닉네임 사용 가능", null);
        }
    }

    // 현재 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseUtil.badRequest("인증된 사용자가 없습니다.", null);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseUtil.badRequest("사용자 정보를 찾을 수 없습니다.", null);
        }

        User user = userOpt.get();
        String profileImageUrl = userService.getProfileImageUrl(user.getUserId());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", user.getUserId());
        responseBody.put("email", user.getEmail());
        responseBody.put("nickname", user.getNickname());
        responseBody.put("role", user.getRole());
        responseBody.put("deposit", user.getDeposit());
        responseBody.put("profileImage", profileImageUrl); // null이어도 OK

        return ResponseEntity.ok(ResponseUtil.success("현재 사용자 정보", responseBody));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(@AuthenticationPrincipal String email,
                                                @RequestParam("file") MultipartFile file) {
        if (email == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "인증된 사용자가 없습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        try {
            String fileUrl = s3Service.uploadProfileImage(file, "PROFILE", user.getUserId());
            return ResponseUtil.success("프로필 이미지 업로드 성공", fileUrl);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "파일 업로드 실패: " + e.getMessage());
        }
    }

    // 회원 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal String email) {
        if (email == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "인증된 사용자가 없습니다.");
        }
        return ResponseUtil.success("회원 정보 조회 완료", userService.getUserInfo(email));
    }

    // 회원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal String email,
                                            @RequestBody UserUpdateRequestDto request) {
        if (email == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "인증된 사용자가 없습니다.");
        }

        Map<String, Object> updatedUser = userService.updateUserInfo(email, request);
        return ResponseUtil.success("회원 정보가 수정되었습니다.", updatedUser);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal String email) {
        System.out.println("📌 Delete 요청 들어옴: " + email);
        if (email == null) {
            System.out.println("❌ 인증된 사용자가 없음");
            return ResponseUtil.unauthorized("인증된 사용자가 없습니다.", null);
        }
        return userService.deleteUser(email);
    }



}
