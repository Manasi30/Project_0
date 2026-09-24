package com.bankofcli.service;

import com.bankofcli.exception.AccountNotFoundException;
import com.bankofcli.exception.InsufficientFundsException;
import com.bankofcli.exception.InvalidCredentialsException;
import com.bankofcli.exception.ServiceUnavailableException;
import com.bankofcli.model.Account;
import com.bankofcli.model.Transaction;
import com.bankofcli.repository.AccountRepository;
import com.bankofcli.repository.TransactionRepository;
import com.bankofcli.util.DatabaseConnectionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankServiceImpl implements BankService {

    private static final Logger logger =
            LoggerFactory.getLogger(BankServiceImpl.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public BankServiceImpl(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }


    @Override
    public Account register(String ownerName, String pin) {

        if (ownerName == null || ownerName.isBlank()) {

            logger.warn(
                    "Registration failed: owner name was empty"
            );

            throw new IllegalArgumentException(
                    "Owner name cannot be empty."
            );
        }

        if (pin == null || pin.length() != 4) {

            logger.warn(
                    "Registration failed: PIN was not 4 digits"
            );

            throw new IllegalArgumentException(
                    "PIN must be exactly 4 digits."
            );
        }

        if (!pin.matches("\\d{4}")) {

            logger.warn(
                    "Registration failed: PIN contained non-numeric characters"
            );

            throw new IllegalArgumentException(
                    "PIN must contain only numbers."
            );
        }

        Account account = new Account(
                0,
                ownerName,
                pin,
                BigDecimal.ZERO,
                null
        );

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            Account createdAccount =
                    accountRepository.insert(
                            account,
                            conn
                    );

            logger.info(
                    "Account {} successfully registered",
                    createdAccount.getAccountId()
            );

            return createdAccount;

        } catch (SQLException e) {

            logger.error(
                    "Database error while registering account",
                    e
            );

            throw new ServiceUnavailableException(
                    "Unable to create account.",
                    e
            );
        }
    }


    @Override
    public Account login(int accountId, String pin) {

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            Account account =
                    accountRepository
                            .findById(accountId, conn)
                            .orElseThrow(() -> {

                                logger.warn(
                                        "Invalid login attempt for account {}",
                                        accountId
                                );

                                return new InvalidCredentialsException(
                                        "Invalid account ID or PIN."
                                );
                            });

            if (!account.getPinHash().equals(pin)) {

                logger.warn(
                        "Invalid PIN or ID for account {}",
                        accountId
                );

                throw new InvalidCredentialsException(
                        "Invalid account ID or PIN."
                );
            }

            logger.info(
                    "Account {} successfully logged in",
                    accountId
            );

            return account;

        } catch (SQLException e) {

            logger.error(
                    "Database error during login for account {}",
                    accountId,
                    e
            );

            throw new ServiceUnavailableException(
                    "Banking services are temporarily unavailable.",
                    e
            );
        }
    }


    @Override
    public BigDecimal getBalance(int accountId) {

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            Account account =
                    accountRepository
                            .findById(accountId, conn)
                            .orElseThrow(() -> {

                                logger.warn(
                                        "Balance request failed: account {} not found",
                                        accountId
                                );

                                return new AccountNotFoundException(
                                        "Account not found."
                                );
                            });

            logger.info(
                    "Account {} checked balance",
                    accountId
            );

            return account.getBalance();

        } catch (SQLException e) {

            logger.error(
                    "Database error retrieving balance for account {}",
                    accountId,
                    e
            );

            throw new ServiceUnavailableException(
                    "Banking services are temporarily unavailable.",
                    e
            );
        }
    }


    @Override
    public BigDecimal deposit(
            int accountId,
            BigDecimal amount
    ) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn(
                    "Deposit rejected for account {}: invalid amount {}",
                    accountId,
                    amount
            );

            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero."
            );
        }

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                Account account =
                        accountRepository
                                .findById(accountId, conn)
                                .orElseThrow(() ->
                                        new AccountNotFoundException(
                                                "Account not found."
                                        )
                                );

                BigDecimal newBalance =
                        account.getBalance().add(amount);

                accountRepository.updateBalance(
                        accountId,
                        newBalance,
                        conn
                );

                Transaction transaction =
                        new Transaction(
                                0,
                                accountId,
                                "DEPOSIT",
                                amount,
                                null,
                                null
                        );

                transactionRepository.insert(
                        transaction,
                        conn
                );

                conn.commit();

                logger.info(
                        "Account {} successfully deposited ${}",
                        accountId,
                        amount
                );

                return newBalance;

            } catch (SQLException | RuntimeException e) {

                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {

            logger.error(
                    "Database error during deposit for account {}",
                    accountId,
                    e
            );

            throw new ServiceUnavailableException(
                    "Unable to complete deposit.",
                    e
            );
        }
    }


    @Override
    public BigDecimal withdraw(
            int accountId,
            BigDecimal amount
    ) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn(
                    "Withdrawal rejected for account {}: invalid amount {}",
                    accountId,
                    amount
            );

            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                Account account =
                        accountRepository
                                .findById(accountId, conn)
                                .orElseThrow(() ->
                                        new AccountNotFoundException(
                                                "Account not found."
                                        )
                                );

                if (account.getBalance()
                        .compareTo(amount) < 0) {

                    logger.warn(
                            "Withdrawal rejected for account {}: insufficient funds",
                            accountId
                    );

                    throw new InsufficientFundsException(
                            "Insufficient funds."
                    );
                }

                BigDecimal newBalance =
                        account.getBalance().subtract(amount);

                accountRepository.updateBalance(
                        accountId,
                        newBalance,
                        conn
                );

                Transaction transaction =
                        new Transaction(
                                0,
                                accountId,
                                "WITHDRAWAL",
                                amount,
                                null,
                                null
                        );

                transactionRepository.insert(
                        transaction,
                        conn
                );

                conn.commit();

                logger.info(
                        "Account {} successfully withdrew ${}",
                        accountId,
                        amount
                );

                return newBalance;

            } catch (SQLException | RuntimeException e) {

                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {

            logger.error(
                    "Database error during withdrawal for account {}",
                    accountId,
                    e
            );

            throw new ServiceUnavailableException(
                    "Unable to complete withdrawal.",
                    e
            );
        }
    }


    @Override
    public void transfer(
            int fromAccountId,
            int toAccountId,
            BigDecimal amount
    ) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn(
                    "Transfer rejected from account {}: invalid amount {}",
                    fromAccountId,
                    amount
            );

            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero."
            );
        }

        if (fromAccountId == toAccountId) {

            logger.warn(
                    "Transfer rejected: account {} attempted transfer to itself",
                    fromAccountId
            );

            throw new IllegalArgumentException(
                    "Cannot transfer money to the same account."
            );
        }

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                Account fromAccount =
                        accountRepository
                                .findById(fromAccountId, conn)
                                .orElseThrow(() ->
                                        new AccountNotFoundException(
                                                "Sender account not found."
                                        )
                                );

                Account toAccount =
                        accountRepository
                                .findById(toAccountId, conn)
                                .orElseThrow(() ->
                                        new AccountNotFoundException(
                                                "Recipient account not found."
                                        )
                                );

                if (fromAccount.getBalance()
                        .compareTo(amount) < 0) {

                    logger.warn(
                            "Transfer rejected from account {} to account {}: insufficient funds",
                            fromAccountId,
                            toAccountId
                    );

                    throw new InsufficientFundsException(
                            "Insufficient funds."
                    );
                }

                BigDecimal newFromBalance =
                        fromAccount
                                .getBalance()
                                .subtract(amount);

                BigDecimal newToBalance =
                        toAccount
                                .getBalance()
                                .add(amount);

                accountRepository.updateBalance(
                        fromAccountId,
                        newFromBalance,
                        conn
                );

                accountRepository.updateBalance(
                        toAccountId,
                        newToBalance,
                        conn
                );

                Transaction senderTransaction =
                        new Transaction(
                                0,
                                fromAccountId,
                                "TRANSFER_OUT",
                                amount,
                                toAccountId,
                                null
                        );

                transactionRepository.insert(
                        senderTransaction,
                        conn
                );

                Transaction receiverTransaction =
                        new Transaction(
                                0,
                                toAccountId,
                                "TRANSFER_IN",
                                amount,
                                fromAccountId,
                                null
                        );

                transactionRepository.insert(
                        receiverTransaction,
                        conn
                );

                conn.commit();

                logger.info(
                        "Transfer of ${} completed from account {} to account {}",
                        amount,
                        fromAccountId,
                        toAccountId
                );

            } catch (SQLException | RuntimeException e) {

                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {

            logger.error(
                    "Database error during transfer from account {} to account {}",
                    fromAccountId,
                    toAccountId,
                    e
            );

            throw new ServiceUnavailableException(
                    "Unable to complete the transfer.",
                    e
            );
        }
    }


    @Override
    public List<Transaction> getRecentTransactions(
            int accountId
    ) {

        try (Connection conn =
                     DatabaseConnectionManager.getConnection()) {

            List<Transaction> transactions =
                    transactionRepository
                            .findRecentByAccountId(
                                    accountId,
                                    10,
                                    conn
                            );

            logger.info(
                    "Account {} viewed recent transaction history",
                    accountId
            );

            return transactions;

        } catch (SQLException e) {

            logger.error(
                    "Database error while retrieving transaction history for account {}",
                    accountId,
                    e
            );

            throw new ServiceUnavailableException(
                    "Banking services are temporarily unavailable.",
                    e
            );
        }
    }
}