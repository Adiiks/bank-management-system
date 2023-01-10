package com.adrian.bank.management.system.converter;

import com.adrian.bank.management.system.dto.AccountInformation;
import com.adrian.bank.management.system.entity.Account;

public class AccountConverter {

    public static AccountInformation convertToDto(Account account) {
        return new AccountInformation(account.getId(), account.getOpeningDate(), account.getBalance());
    }
}
