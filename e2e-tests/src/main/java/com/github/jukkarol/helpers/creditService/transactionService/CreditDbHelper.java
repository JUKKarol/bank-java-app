package com.github.jukkarol.helpers.creditService.transactionService;

import com.github.jukkarol.config.TestConfig;
import com.github.jukkarol.model.Credit;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreditDbHelper {

    private final TestConfig config;

    public CreditDbHelper(TestConfig config) {
        this.config = config;
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                config.getCreditService().getDatasource().getUrl(),
                config.getCreditService().getDatasource().getUsername(),
                config.getCreditService().getDatasource().getPassword()
        );
    }

    public void deleteCreditsByAccountNumber(String accountNumber) throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement deleteCreditsHistories = connection.prepareStatement(
                    "DELETE FROM credits_history WHERE credit_id IN (SELECT id FROM credits WHERE account_number = ?)");
            deleteCreditsHistories.setString(1, accountNumber);
            deleteCreditsHistories.execute();

            PreparedStatement deleteCredits = connection.prepareStatement(
                    "DELETE FROM credits WHERE account_number = ?");
            deleteCredits.setString(1, accountNumber);
            deleteCredits.execute();
        }
    }

    public List<Credit> getCreditsByAccountNumber(String accountNumber) throws Exception {
        try (Connection connection = getConnection()) {
            PreparedStatement getCredits = connection.prepareStatement("SELECT * FROM credits WHERE account_number = ?");
            getCredits.setString(1, accountNumber);

            ResultSet rs = getCredits.executeQuery();

            List<Credit> credits = new ArrayList<>();
            while (rs.next()) {
                Credit credit = new Credit();
                credit.setId(rs.getLong("id"));
                credit.setAccountNumber(rs.getString("account_number"));
                credit.setAmountTotal(rs.getBigDecimal("amount_total"));
                credit.setAmountMonthly(rs.getBigDecimal("amount_monthly"));
                credit.setInstallmentTotal(rs.getInt("installment_total"));
                credit.setInstallmentLeft(rs.getInt("installment_left"));
                credit.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                credit.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                credits.add(credit);
            }

            return credits;
        }
    }
}