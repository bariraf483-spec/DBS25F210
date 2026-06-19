package org.example.dao;

import org.example.domain.User;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Authenticates a user by validating their username and password against the database records.
     * @param username The entered login username
     * @param password The entered login password
     * @return User object populated with data if valid, or null if credentials fail
     */
    public User authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Note: For a production app, we would use password hashing (like BCrypt)!

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create and return the user object if found
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getInt("role_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database exception during user authentication!");
            e.printStackTrace();
        }

        return null; // Returns null if no matching username/password pair is found
    }
}