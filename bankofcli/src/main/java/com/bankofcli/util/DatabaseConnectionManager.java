package com.bankofcli.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {

    private static final String CONF_FILE = "db.properties";
    private static Properties props;

    static {
        loadProperties();
    }

    private static void loadProperties(){
        props = new Properties();
        try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream(CONF_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONF_FILE + " in the classpath." + "Copy db.properties.example to db.properties and fill in your credentials.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String username = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }
    
}
