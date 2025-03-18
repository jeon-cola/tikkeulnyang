package com.c107.user.controller;

import com.c107.auth.entity.LoginUserEntity;
import com.c107.auth.repository.LoginUserRepository;
import com.c107.common.util.ResponseUtil;
import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginUserRepository loginUserRepository; // 추가

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        Optional<LoginUserEntity> existingUser = loginUserRepository.findByNickname(nickname);
        if (existingUser.isPresent()) {
            // 닉네임 중복
            return ResponseUtil.badRequest("사용중인 닉네임입니다.", null);
        } else {
            // 사용 가능
            return ResponseUtil.success("닉네임 사용 가능", null);
        }
    }

}
