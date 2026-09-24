package com.bankofcli.api;

import com.bankofcli.exception.*;
import com.bankofcli.model.*;
import com.bankofcli.service.BankService;
import java.math.BigDecimal;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;


public class BankCli {
    private final BankService bankService;
    private final Scanner scanner;

    public BankCli(BankService bankService){
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=========================");
            System.out.println("       BANK OF CLI");
            System.out.println("=========================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = readInt("Choose an option: ");
            final Logger logger = LoggerFactory.getLogger(BankCli.class);

            switch (choice) {
                case 1:
                    register();
                    logger.info("Account was made with account id is: ");
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Please choose a valid option.");
            }
        }
        scanner.close();
    }

    private void register() {

        System.out.println();
        System.out.println("--- Register ---");
    
        System.out.print("Enter your name: ");
        String ownerName = scanner.nextLine();
    
        System.out.print("Choose a 4-digit PIN: ");
        String pin = scanner.nextLine();
    
        try {
    
            Account account =
                    bankService.register(
                            ownerName,
                            pin
                    );
    
            System.out.println();
            System.out.println(
                    "Account created successfully!"
            );
    
            System.out.println(
                    "Your Account ID is: "
                            + account.getAccountId()
            );
    
            System.out.println(
                    "Remember your Account ID and PIN."
            );
    
        } catch (IllegalArgumentException e) {
    
            System.out.println(
                    "Registration failed: "
                            + e.getMessage()
            );
    
        } catch (ServiceUnavailableException e) {
    
            System.out.println(
                    "Banking services are temporarily unavailable."
            );
        }
    }


    private void login() {

        System.out.println();
        System.out.println("--- Login ---");

        int accountId =
                readInt("Account ID: ");

        System.out.print("PIN: ");
        String pin = scanner.nextLine();

        try {

            Account account =
                    bankService.login(
                            accountId,
                            pin
                    );

            System.out.println();
            System.out.println(
                    "Login successful."
            );

            accountMenu(account);

        } catch (InvalidCredentialsException e) {

            System.out.println(
                    e.getMessage()
            );

        } catch (ServiceUnavailableException e) {

            System.out.println(
                    "Banking services are temporarily unavailable."
            );
        }
    }


    private void accountMenu(Account account) {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println();
            System.out.println("=========================");
            System.out.println(
                    "Welcome, "
                            + account.getOwnerName()
            );
            System.out.println("=========================");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");

            int choice =
                    readInt("Choose an option: ");

            switch (choice) {

                case 1:
                    checkBalance(
                            account.getAccountId()
                    );
                    break;

                case 2:
                    deposit(
                            account.getAccountId()
                    );
                    break;

                case 3:
                    withdraw(
                            account.getAccountId()
                    );
                    break;

                case 4:
                    transfer(
                            account.getAccountId()
                    );
                    break;

                case 5:
                    showTransactionHistory(
                            account.getAccountId()
                    );
                    break;

                case 6:
                    loggedIn = false;
                    System.out.println(
                            "Logged out successfully."
                    );
                    break;

                default:
                    System.out.println(
                            "Please choose a valid option."
                    );
            }
        }
    }


    private void checkBalance(int accountId) {

        try {

            BigDecimal balance =
                    bankService.getBalance(
                            accountId
                    );

            System.out.println(
                    "Current balance: $"
                            + balance
            );

        } catch (AccountNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );

        } catch (ServiceUnavailableException e) {

            System.out.println(
                    "Banking services are temporarily unavailable."
            );
        }
    }


    private void deposit(int accountId) {

        BigDecimal amount =
                readAmount(
                        "Deposit amount: $"
                );

        try {

            BigDecimal newBalance =
                    bankService.deposit(
                            accountId,
                            amount
                    );

            System.out.println(
                    "Deposit successful."
            );

            System.out.println(
                    "New balance: $"
                            + newBalance
            );

        } catch (IllegalArgumentException |
                 AccountNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );

        } catch (ServiceUnavailableException e) {

            System.out.println(
                    "Unable to complete deposit."
            );
        }
    }


    private void withdraw(int accountId) {

        BigDecimal amount =
                readAmount(
                        "Withdrawal amount: $"
                );

        try {

            BigDecimal newBalance =
                    bankService.withdraw(
                            accountId,
                            amount
                    );

            System.out.println(
                    "Withdrawal successful."
            );

            System.out.println(
                    "New balance: $"
                            + newBalance
            );

        } catch (IllegalArgumentException |
                 AccountNotFoundException |
                 InsufficientFundsException e) {

            System.out.println(
                    e.getMessage()
            );

        } catch (ServiceUnavailableException e) {

            System.out.println(
                    "Unable to complete withdrawal."
            );
        }
    }


    private void transfer(int fromAccountId) {

        int toAccountId =
                readInt(
                        "Recipient Account ID: "
                );

        BigDecimal amount =
                readAmount(
                        "Transfer amount: $"
                );

        try {

            bankService.transfer(
                    fromAccountId,
                    toAccountId,
                    amount
            );

            System.out.println(
                    "Transfer successful."
            );

        } catch (IllegalArgumentException |
                 AccountNotFoundException |
                 InsufficientFundsException e) {

            System.out.println(
                    e.getMessage()
            );

        } catch (ServiceUnavailableException e) {

            System.out.println(
                    "Unable to complete transfer."
            );
        }
    }


    private void showTransactionHistory(
            int accountId
    ) {

        try {

            List<Transaction> transactions =
                    bankService
                            .getRecentTransactions(
                                    accountId
                            );

            System.out.println();
            System.out.println(
                    "--- Recent Transactions ---"
            );

            if (transactions.isEmpty()) {

                System.out.println(
                        "No transactions found."
                );

                return;
            }

            for (Transaction transaction :
                    transactions) {

                System.out.println(
                        transaction.getType()
                                + " | $"
                                + transaction.getAmount()
                                + " | "
                                + transaction.getCreatedAt()
                );
            }

        } catch (ServiceUnavailableException e) {

            System.out.println(
                    "Unable to retrieve transaction history."
            );
        }
    }


    private int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine();

            try {

                return Integer.parseInt(
                        input
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }


    private BigDecimal readAmount(
            String message
    ) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine();

            try {

                return new BigDecimal(
                        input
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid amount."
                );
            }
        }
    }
}

    

