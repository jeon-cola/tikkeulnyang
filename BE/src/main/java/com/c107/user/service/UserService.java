package com.c107.user.service;

import com.c107.auth.service.FinanceService;
import com.c107.common.exception.CustomException;
import com.c107.common.exception.ErrorCode;
import com.c107.common.util.ResponseUtil;
import com.c107.s3.repository.S3Repository;
import com.c107.user.dto.UserRegistrationRequestDto;
import com.c107.user.dto.UserUpdateRequestDto;
import com.c107.user.entity.User;
import com.c107.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.c107.s3.entity.S3Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
                    .name(request.getName())
                    .birthDate(request.getBirthDate())
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

    // 사용자 정보 조회: 캐시 적용 (읽기 전용)
    @Cacheable(value = "userInfoCache", key = "#email", unless = "#result == null")
    public Map<String, Object> getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getUserId());
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("name", user.getName());
        userInfo.put("birthDate", user.getBirthDate());
        userInfo.put("role", user.getRole());
        userInfo.put("deposit", user.getDeposit());
        userInfo.put("profileImage", getProfileImageUrl(user.getUserId()));

        return userInfo;
    }

    // 사용자 정보 수정 시, 캐시 무효화 적용 (수정된 정보가 바로 반영되어야 함)
    @Caching(evict = {
            @CacheEvict(value = "userInfoCache", key = "#email")
    })
    public Map<String, Object> updateUserInfo(String email, UserUpdateRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        // 닉네임 중복 체크
        userRepository.findByNickname(request.getNickname())
                .filter(u -> !u.getEmail().equals(email))
                .ifPresent(u -> {
                    throw new CustomException(ErrorCode.VALIDATION_FAILED, "이미 사용 중인 닉네임입니다.");
                });

        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setBirthDate(request.getBirthDate());

        userRepository.save(user);

        // 수정된 정보 반환
        Map<String, Object> updatedUserInfo = new HashMap<>();
        updatedUserInfo.put("id", user.getUserId());
        updatedUserInfo.put("email", user.getEmail());
        updatedUserInfo.put("nickname", user.getNickname());
        updatedUserInfo.put("name", user.getName());
        updatedUserInfo.put("birthDate", user.getBirthDate());
        updatedUserInfo.put("role", user.getRole());
        updatedUserInfo.put("deposit", user.getDeposit());
        updatedUserInfo.put("profileImage", getProfileImageUrl(user.getUserId()));

        return updatedUserInfo;
    }

    // 회원 탈퇴 시, 캐시 무효화 적용
    @Caching(evict = {
            @CacheEvict(value = "userInfoCache", key = "#email")
    })
    public ResponseEntity<?> deleteUser(String email) {
        System.out.println("🔍 이메일로 유저 조회: " + email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        System.out.println("찾았다");
        if (userOpt.isEmpty()) {
            return ResponseUtil.badRequest("사용자 정보를 찾을 수 없습니다.", null);
        }
        User user = userOpt.get();
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            System.out.println("⚠️ 이미 탈퇴한 사용자");
            return ResponseUtil.badRequest("이미 탈퇴한 사용자입니다.", null);
        }
        System.out.println("📝 탈퇴 처리 중...");
        user.setIsDeleted(true);
        userRepository.save(user);
        System.out.println("✅ 탈퇴 완료");
        return ResponseUtil.success("회원 탈퇴가 완료되었습니다.", null);
    }
}
