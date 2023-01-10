package com.adrian.bank.management.system.repository;

import com.adrian.bank.management.system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByIdAndUserUsername(Integer accountId, String username);
}
