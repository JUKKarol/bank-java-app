package com.github.jukkarol.helpers.authService;

import com.github.jukkarol.config.TestConfig;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class UserDbHelper {

    private final TestConfig config;

    public UserDbHelper(TestConfig config) {
        this.config = config;
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                config.getAuthService().getDatasource().getUrl(),
                config.getAuthService().getDatasource().getUsername(),
                config.getAuthService().getDatasource().getPassword()
        );
    }

    public void removeUser(String email) throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement deleteRoles = connection.prepareStatement("DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE email = ?)");
            deleteRoles.setString(1, email);
            deleteRoles.execute();

            PreparedStatement deleteUsers = connection.prepareStatement("DELETE FROM users WHERE email = ?");
            deleteUsers.setString(1, email);
            deleteUsers.execute();
        }
    }
}