package com.bankofcli.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    private int accountId;
    private String ownerName;
    private String pinHash;
    private BigDecimal balance;
    private Timestamp createdAt;

    public Account() {
    }

    public Account(int accountId, String ownerName, String pinHash, BigDecimal balance, Timestamp createdAt) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.pinHash = pinHash;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}