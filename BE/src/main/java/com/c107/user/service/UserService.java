package com.c107.user.service;

import com.c107.auth.service.FinanceService;
import com.c107.common.util.ResponseUtil;
import com.c107.s3.repository.S3Repository;
import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.c107.s3.entity.S3Entity;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FinanceService financeService;
    private final S3Repository s3Repository;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;


    public String getProfileImageUrl(Integer userId) {
        return s3Repository.findTopByUsageTypeAndUsageIdOrderByCreatedAtDesc("PROFILE", userId)
                .map(S3Entity::getUrl)
                .orElse(defaultProfileImageUrl);
    }


    public ResponseEntity<?> registerUser(UserRegistrationRequestDto request) {
        // 1. DB에서 유저 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            // 신규 유저 생성 (필요에 따라 request의 name, birthDate 등도 저장 가능)
            user = User.builder()
                    .email(request.getEmail())
                    .nickname(request.getNickname())
                    .name(request.getName())                  // ✅ 이름 저장
                    .birthDate(request.getBirthDate())        // ✅ 생년월일 저장
                    .role("USER")
                    .build();

        } else {
            // 기존 유저 -> 닉네임 업데이트
            user.setNickname(request.getNickname());
        }

        // 2. 금융 API 계정 확인 및 userKey 저장
        if (user.getFinanceUserKey() == null || user.getFinanceUserKey().isBlank()) {
            String financeUserKey = financeService.getFinanceUserKey(user.getEmail());
            if (financeUserKey != null && !financeUserKey.isBlank()) {
                user.setFinanceUserKey(financeUserKey);
            }
        }

        // 3. DB 저장 (financeUserKey 포함)
        userRepository.save(user);

        return ResponseUtil.success("회원가입 완료", user.getEmail());
    }
}
