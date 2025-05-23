package com.c107.accounts.repository;

import com.c107.accounts.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findFirstByUserIdOrderByCreatedAtAsc(Integer userId);

    List<Account> findByUserId(Integer loggedInUserId);

    Optional<Account> findByUserIdAndRepresentativeTrue(Integer loggedInUserId);
    Optional<Account> findByUserIdIsNullAndAccountType(String accountType);
}
