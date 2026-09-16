package com.bankofcli;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/project0DB";
        String username = "msm";
        String password = "yourPassword";
        try {
            Connection connection =
                            DriverManager.getConnection(url,username,password);

            System.out.println("Connected to PostgresSQL");

            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
