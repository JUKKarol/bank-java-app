package com.github.jukkarol.helpers;

import com.github.jukkarol.config.TestConfig;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class AuthDbHelper {

    private final TestConfig config;

    public AuthDbHelper(TestConfig config) {
        this.config = config;
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                config.getAuthService().getDatasource().getUrl(),
                config.getAuthService().getDatasource().getUsername(),
                config.getAuthService().getDatasource().getPassword()
        );
    }

    public void addRoleToUser(String email, String roleName) throws Exception {
        try (Connection connection = getConnection()) {

            PreparedStatement findUser = connection.prepareStatement(
                    "SELECT id FROM users WHERE email = ?");
            findUser.setString(1, email);
            ResultSet userRs = findUser.executeQuery();

            if (!userRs.next()) {
                throw new RuntimeException("User not found: " + email);
            }

            long userId = userRs.getLong("id");

            PreparedStatement findRole = connection.prepareStatement(
                    "SELECT 1 FROM user_roles WHERE user_id = ? AND role = ?");

            findRole.setLong(1, userId);
            findRole.setString(2, roleName);

            ResultSet roleRs = findRole.executeQuery();

            if (roleRs.next()) {
                throw new RuntimeException(
                        "Role already exists: " + roleName + " for user: " + email);
            }

            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO user_roles (user_id, role) VALUES (?, ?)");
            insert.setLong(1, userId);
            insert.setString(2, roleName);
            insert.executeUpdate();
        }
    }

    public void removeRolesFromUser(String email) throws Exception {
        try (Connection connection = getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE email = ?)");
            stmt.setString(1, email);
            stmt.executeUpdate();
        }
    }
}