package com.c107.user.service;

import com.c107.auth.entity.LoginUserEntity;
import com.c107.auth.repository.LoginUserRepository;
import com.c107.auth.service.FinanceService;
import com.c107.common.util.ResponseUtil;
import com.c107.user.dto.UserRegistrationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final LoginUserRepository loginUserRepository;
    private final FinanceService financeService;

    public ResponseEntity<?> registerUser(UserRegistrationRequestDto request) {
        // 1. DB에 유저가 이미 있는지 확인
        LoginUserEntity user = loginUserRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null) {
            // 이미 존재하면 닉네임 등 업데이트
            user.setNickname(request.getNickname());
            // 만약 name, birthDate를 LoginUserEntity에 추가했다면 업데이트
            // user.setName(request.getName());
            // user.setBirthDate(request.getBirthDate());
        } else {
            // DB에 없는 경우 신규 유저 생성
            user = LoginUserEntity.builder()
                    .email(request.getEmail())
                    .nickname(request.getNickname())
                    .role("USER")
                    .build();
            // name, birthDate도 엔티티에 맞춰서 저장
        }

        // 2. 금융 API 계정도 없는 경우 새로 생성
        if (user.getFinanceUserKey() == null || user.getFinanceUserKey().isBlank()) {
            String financeUserKey = financeService.createFinanceUser(user.getEmail(), user.getNickname());
            user.setFinanceUserKey(financeUserKey);
        }

        // 3. DB 저장
        loginUserRepository.save(user);

        return ResponseUtil.success("회원가입 완료", user.getEmail());
    }
}
