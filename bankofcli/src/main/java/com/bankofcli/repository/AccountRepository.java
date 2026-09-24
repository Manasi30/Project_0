package com.bankofcli.repository;

import com.bankofcli.model.Account;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


public interface AccountRepository {
    Optional<Account> findById(int accountId, Connection conn) throws SQLException;
    Account insert(Account account, Connection conn) throws SQLException;
    void updateBalance(int accountId, BigDecimal newBalance, Connection conn) throws SQLException;
    
    
}
