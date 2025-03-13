package com.c107.user.controller;

import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }
}
