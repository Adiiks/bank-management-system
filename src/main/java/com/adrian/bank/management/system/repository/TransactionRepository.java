package com.adrian.bank.management.system.repository;

import com.adrian.bank.management.system.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByAccount_IdAndAccount_User_Username(Integer accountId, String username, Pageable pageable);
}
