package com.bankofcli.repository;

import com.bankofcli.model.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;



public interface TransactionRepository {
    Transaction insert(Transaction transaction, Connection conn) throws SQLException;
    List<Transaction> findRecentByAccountId(int accountId, int limit, Connection conn) throws SQLException;
    
}
