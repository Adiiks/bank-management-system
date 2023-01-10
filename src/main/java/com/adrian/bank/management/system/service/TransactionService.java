package com.adrian.bank.management.system.service;

import com.adrian.bank.management.system.converter.TransactionConverter;
import com.adrian.bank.management.system.dto.TransactionDTO;
import com.adrian.bank.management.system.entity.Transaction;
import com.adrian.bank.management.system.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<TransactionDTO> getTransactionsForUserAccount(String username, Integer accountId, Pageable pageable) {
        // Get from db all transactions belong to user's account
       Page<Transaction> transactionsPage =
               transactionRepository.findByAccount_IdAndAccount_User_Username(accountId, username, pageable);

       // Map transactions to dto objects
        List<TransactionDTO> transactionDTOList = transactionsPage.getContent()
                .stream()
                .map(TransactionConverter::convertToDto)
                .toList();

        // Return page of transactions
       return new PageImpl<>(transactionDTOList, transactionsPage.getPageable(), transactionsPage.getTotalElements());
    }

    @Transactional
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
