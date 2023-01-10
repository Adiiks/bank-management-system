package com.adrian.bank.management.system.converter;

import com.adrian.bank.management.system.dto.TransactionDTO;
import com.adrian.bank.management.system.entity.Transaction;

public class TransactionConverter {

    public static TransactionDTO convertToDto(Transaction transaction) {
        return new TransactionDTO(transaction.getId(), transaction.getDateTime(), transaction.getAmount(),
                transaction.getType());
    }
}
