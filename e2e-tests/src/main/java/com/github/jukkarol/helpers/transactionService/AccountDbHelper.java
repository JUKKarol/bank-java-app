package com.github.jukkarol.helpers.transactionService;

import com.github.jukkarol.config.TestConfig;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@Component
public class AccountDbHelper {

    private final TestConfig config;

    public AccountDbHelper(TestConfig config) {
        this.config = config;
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                config.getTransactionService().getDatasource().getUrl(),
                config.getTransactionService().getDatasource().getUsername(),
                config.getTransactionService().getDatasource().getPassword()
        );
    }

    public void removeAccount(String accountNumber) throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement deleteAccountNumbers = connection.prepareStatement("DELETE FROM accounts WHERE account_number = ?");
            deleteAccountNumbers.setString(1, accountNumber);
            deleteAccountNumbers.execute();
        }
    }
}