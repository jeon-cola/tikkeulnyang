package com.c107.accounts.repository;

import com.c107.accounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    // 추가적인 쿼리 메서드가 필요하면 정의 가능
}
