package com.bankofcli.service;

import com.bankofcli.model.Account;
import com.bankofcli.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface BankService {
    Account register( String ownerName, String pin);
    Account login(int accountId, String pin);
    BigDecimal getBalance(int accountId);
    BigDecimal deposit(int accountId, BigDecimal amount);
    BigDecimal withdraw(int accountId, BigDecimal amount);
    void transfer(int fromAccountId, int toAccountId, BigDecimal amount);
    List<Transaction> getRecentTransactions(int accountId);
    
}
