package com.bankofcli.repository;

import com.bankofcli.model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class TransactionRepositoryImpl implements TransactionRepository {

    @Override 
    public Transaction insert(Transaction transaction, Connection conn) throws SQLException{
        String sql = "INSERT INTO transactions" + "(account_id, type, amount, related_account_id) " + "VALUES(?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getType());
            stmt.setBigDecimal(3, transaction.getAmount());
            if (transaction.getRelatedAccountId() != null){
                stmt.setInt(4, transaction.getRelatedAccountId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()){
                if (keys.next()){
                    transaction.setTransactionId(keys.getInt(1));
                }
            }
        }
        return transaction;

    }
    @Override
    public List<Transaction> findRecentByAccountId(
            int accountId,
            int limit,
            Connection conn
    ) throws SQLException {

        String sql =
                "SELECT * FROM transactions " +
                "WHERE account_id = ? " +
                "ORDER BY created_at DESC " +
                "LIMIT ?";

        List<Transaction> transactions =
                new ArrayList<>();

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setInt(2, limit);

            try (ResultSet rs =
                     stmt.executeQuery()) {

                while (rs.next()) {

                    transactions.add(
                            mapRow(rs)
                    );
                }
            }
        }

        return transactions;
    }


    private Transaction mapRow(
            ResultSet rs
    ) throws SQLException {

        Integer relatedAccountId =
                rs.getObject(
                        "related_account_id",
                        Integer.class
                );

        return new Transaction(
                rs.getInt("transaction_id"),
                rs.getInt("account_id"),
                rs.getString("type"),
                rs.getBigDecimal("amount"),
                relatedAccountId,
                rs.getTimestamp("created_at")
        );
    }
    
    
}
