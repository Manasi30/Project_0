package com.bankofcli.repository;

import com.bankofcli.model.Account;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;



public class AccountRepositoryImpl implements AccountRepository {
    @Override 
    public Optional <Account> findById(int accountId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";   
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next ()){
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        }
        catch(SQLException e)
        {
            return null;
        }
    }

    @Override 
    public Account insert(Account account, Connection conn) throws SQLException{
        String sql = "INSERT INTO accounts (owner_name, pin_hash, balance)" + "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, account.getOwnerName());
            stmt.setString(2, account.getPinHash());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()){
                    account.setAccountId(keys.getInt(1));
                }
            }
        }

        return account;

    }

    @Override 
    public void updateBalance(int accountId, BigDecimal newBalance, Connection conn) throws SQLException{
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();

        }

    }

    private Account mapRow(ResultSet rs) throws SQLException{
        return new Account(
            rs.getInt("account_id"),
            rs.getString("owner_name"),
            rs.getString("pin_hash"),
            rs.getBigDecimal("balance"),
            rs.getTimestamp("created_at")
        );
    }

    
}
