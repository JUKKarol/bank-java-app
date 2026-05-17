package com.github.jukkarol.tests.creditService.atmService;

import com.github.jukkarol.clients.atmService.ATMApiClient;
import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.creditService.CreditApiClient;
import com.github.jukkarol.clients.creditService.ToolsApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.clients.transactionService.TransactionApiClient;
import com.github.jukkarol.helpers.authService.RoleDbHelper;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import com.github.jukkarol.helpers.creditService.transactionService.CreditDbHelper;
import com.github.jukkarol.helpers.transactionService.AccountDbHelper;
import com.github.jukkarol.model.Credit;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreditTests {

    @Autowired
    private UserDbHelper userDbHelper;

    @Autowired
    private RoleDbHelper authDbHelper;

    @Autowired
    private AccountDbHelper accountDbHelper;

    @Autowired
    private CreditDbHelper creditDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private TransactionApiClient transactionApiClient;

    @Autowired
    private CreditApiClient creditApiClient;

    @Autowired
    private ToolsApiClient toolsApiClient;

    String randomUUID1 = UUID.randomUUID().toString().substring(10);
    String email= randomUUID1 + "@gmail.com";
    String name = randomUUID1;
    String password = "password!123";

    String randomUUID2 = UUID.randomUUID().toString().substring(10);
    String emailEmployee = randomUUID2 + "@gmail.com";
    String nameEmployee = randomUUID2;
    String passwordEmployee = "password!123";

    private String userToken, employeeToken;
    private String accountNumber;
    private String balance;

    BigDecimal transferAmount = new BigDecimal("100.12");

    @BeforeEach
    void setUp() throws Exception {
        authApiClient.signup(email, name, password);
        authApiClient.signup(emailEmployee, nameEmployee, passwordEmployee);

        authDbHelper.addRoleToUser(emailEmployee, "Employee");
        authDbHelper.addRoleToUser(emailEmployee, "Admin");

        userToken = authApiClient.login(email, password).jsonPath().getString("token");
        employeeToken = authApiClient.login(emailEmployee, passwordEmployee).jsonPath().getString("token");

        var acc = accountApiClient.createAccount(userToken);
        accountNumber = acc.jsonPath().getString("accountNumber");
        balance = acc.jsonPath().getString("balance");
    }

    @AfterEach
    void tearDown() throws Exception {
        userDbHelper.removeUser(email);
        userDbHelper.removeUser(emailEmployee);
        accountDbHelper.removeAccount(accountNumber);
    }

    @Test
    void shouldCreateCredit() throws Exception{
        //create credit
        int InstallmentTotal = 1;
        BigDecimal amountTotal = transferAmount.multiply(new BigDecimal(InstallmentTotal));
        Response responseCreateCredit = creditApiClient.createCredit(employeeToken, accountNumber, amountTotal, transferAmount);
        assertThat(responseCreateCredit.statusCode()).isIn(200);

        List<Long> ids = new ArrayList<>();
        ids.add(responseCreateCredit.jsonPath().getLong("id"));
        assertThat(responseCreateCredit.jsonPath().getString("installmentTotal")).isEqualTo(String.valueOf(InstallmentTotal));
        assertThat(responseCreateCredit.jsonPath().getString("installmentLeft")).isEqualTo(String.valueOf(InstallmentTotal));

        //check is credit created in db
        List<Credit> getCreditsBeforeTimer = creditDbHelper.getCreditsByAccountNumber(accountNumber);
        assertThat((long) getCreditsBeforeTimer.size()).isEqualTo(1);

        //trigger credit iteration
        toolsApiClient.processSpecifiedCreditsInstallments(employeeToken, ids);

        //wait for kafka process
        Thread.sleep(500);

        List<Credit> getCreditsAfterTimer = creditDbHelper.getCreditsByAccountNumber(accountNumber);
        assertThat(getCreditsAfterTimer.getFirst().getInstallmentLeft()).isEqualTo(InstallmentTotal - 1);
        assertThat(getCreditsAfterTimer.getFirst().getInstallmentTotal()).isEqualTo(InstallmentTotal);

        //check is transaction created
        Response responseGetTransfers = transactionApiClient.getAccountTransactions(userToken, accountNumber);
        assertThat(responseGetTransfers.statusCode()).isIn(200);
        assertThat(responseGetTransfers.jsonPath().getString("content.amount")).isEqualTo("[" + transferAmount.toString() + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.balanceAfterTransaction")).isEqualTo("[" + new BigDecimal(balance).subtract(transferAmount)  + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.fromAccountNumber")).isEqualTo("[" + "CREDIT" + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.toAccountNumber")).isEqualTo("[" + accountNumber + "]");

        //delete credit
        creditDbHelper.deleteCreditsByAccountNumber(accountNumber);
    }

    //test where credit will have more than one installment

    //test where amountMonthly is greater than amountTotal
}
