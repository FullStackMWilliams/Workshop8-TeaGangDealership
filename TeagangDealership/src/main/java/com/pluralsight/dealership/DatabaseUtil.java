package com.pluralsight.dealership;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    // Load .env once
    private static final Dotenv dotenv = Dotenv.load();

    // These map directly to your existing .env keys
    private static final String URL  = dotenv.get("URL");      // JDBC URL
    private static final String USER = dotenv.get("USER");     // DB user
    private static final String PASS = dotenv.get("PASSWORD"); // DB password

    static {
        try {
            // Supabase = Postgres, so we need the Postgres driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found on classpath!", e);
        }
    }

    /**
     * Use this everywhere in your app to get a DB connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Optional: run this manually to test your connection.
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
