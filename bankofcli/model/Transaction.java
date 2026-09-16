package com.bankofcli.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private int accountId;
    private String type;
    private BigDecimal amount;
    private Integer relatedAccountId;
    private Timestamp createdAt;


    public Transaction() {
    }

    public Transaction(int transactionId, int accountId, String type, BigDecimal amount, Integer relatedAccountId, Timestamp createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.relatedAccountId = relatedAccountId;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }

    public void setRelatedAccountId(Integer relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }   

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
