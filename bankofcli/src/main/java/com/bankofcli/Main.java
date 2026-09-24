package com.bankofcli;

import com.bankofcli.api.BankCli;
import com.bankofcli.repository.AccountRepository;
import com.bankofcli.repository.AccountRepositoryImpl;
import com.bankofcli.repository.TransactionRepository;
import com.bankofcli.repository.TransactionRepositoryImpl;
import com.bankofcli.service.BankService;
import com.bankofcli.service.BankServiceImpl;

public class Main {

    public static void main(String[] args) {

        AccountRepository accountRepository = new AccountRepositoryImpl();

        TransactionRepository transactionRepository = new TransactionRepositoryImpl();

        BankService bankService = new BankServiceImpl(accountRepository, transactionRepository);

        BankCli bankCli = new BankCli(bankService);

        bankCli.start();
    }
}