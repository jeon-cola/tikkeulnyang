package com.c107.accounts.repository;

import com.c107.accounts.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Integer> {
    // 추가적인 조회 메서드가 필요하면 여기에 정의합니다.
}
