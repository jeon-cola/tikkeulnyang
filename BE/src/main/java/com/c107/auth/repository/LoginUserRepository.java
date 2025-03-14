package com.c107.auth.repository;

import com.c107.auth.entity.LoginUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoginUserRepository extends JpaRepository<LoginUserEntity, Long> {
    Optional<LoginUserEntity> findByEmail(String email);
}
