package com.bankofcli.repository;


import com.bankofcli.model.Account;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AccountRepositoryImplTest {
    @Test 
    void findByIdShouldReturnAccountWhenAccountExists()
    throws Exception{
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        AccountRepositoryImpl repository = new AccountRepositoryImpl();
        when(conn.prepareStatement("SELECT * FROM accounts WHERE account_id = ?")).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("account_id")).thenReturn(1);
        when(rs.getString("owner_name")).thenReturn("Mango");
        when(rs.getString("pin_hash")).thenReturn("5678");
        when(rs.getBigDecimal("balance")).thenReturn(new BigDecimal("100.00"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        when(rs.getTimestamp("created_at")).thenReturn(timestamp);
    Optional<Account> result =
    repository.findById(1, conn);
            
    assertTrue(result.isPresent());
            
    assertEquals(1, result.get().getAccountId());
            
    assertEquals("Mango", result.get().getOwnerName());
            
    assertEquals("5678", result.get().getPinHash());
            
    assertEquals(new BigDecimal("100.00"), result.get().getBalance());

    }
    @Test
    void findByIdShouldReturnEmptyWhenAccountDoesNotExist()
            throws Exception {

        // Arrange
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        AccountRepositoryImpl repository = new AccountRepositoryImpl();

        when(conn.prepareStatement("SELECT * FROM accounts WHERE account_id = ?")).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Optional<Account> result = repository.findById(999, conn);

        assertTrue(result.isEmpty());
    }
    
}
