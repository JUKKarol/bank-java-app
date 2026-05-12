package com.github.jukkarol.tests.atmService;

import com.github.jukkarol.clients.atmService.ATMApiClient;
import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.clients.transactionService.TransactionApiClient;
import com.github.jukkarol.helpers.authService.RoleDbHelper;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import com.github.jukkarol.helpers.transactionService.AccountDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DepositTests {

    @Autowired
    private UserDbHelper userDbHelper;

    @Autowired
    private RoleDbHelper authDbHelper;

    @Autowired
    private AccountDbHelper accountDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private TransactionApiClient transactionApiClient;

    @Autowired
    private ATMApiClient atmApiClient;

    String randomUUID1 = UUID.randomUUID().toString().substring(10);
    String email= randomUUID1 + "@gmail.com";
    String name = randomUUID1;
    String password = "password!123";

    String randomUUID2 = UUID.randomUUID().toString().substring(10);
    String emailATM = randomUUID2 + "@gmail.com";
    String nameATM = randomUUID2;
    String passwordATM = "password!123";

    private String userToken, ATMToken;
    private String accountNumber;
    private String balance;

    BigDecimal transferAmount = new BigDecimal("100.12");

    @BeforeEach
    void setUp() throws Exception {
        authApiClient.signup(email, name, password);
        authApiClient.signup(emailATM, nameATM, passwordATM);

        authDbHelper.addRoleToUser(emailATM, "ATM");

        userToken = authApiClient.login(email, password).jsonPath().getString("token");
        ATMToken = authApiClient.login(emailATM, passwordATM).jsonPath().getString("token");

        var acc = accountApiClient.createAccount(userToken);
        accountNumber = acc.jsonPath().getString("accountNumber");
        balance = acc.jsonPath().getString("balance");
    }

    @AfterEach
    void tearDown() throws Exception {
        userDbHelper.removeUser(email);
        userDbHelper.removeUser(emailATM);
        accountDbHelper.removeAccount(accountNumber);
    }

    @Test
    void shouldMakeDeposit() throws Exception{
        //make deposit
        Response responseMakeDeposit = atmApiClient.makeDeposit(ATMToken, accountNumber, transferAmount);
        assertThat(responseMakeDeposit.statusCode()).isIn(200);
        assertThat(responseMakeDeposit.jsonPath().getString("amount")).isEqualTo(transferAmount.toString());
        assertThat(responseMakeDeposit.jsonPath().getString("accountNumber")).isEqualTo(accountNumber);

        //wait for kafka process
        //Thread.sleep(500);

        //get all user transfers
        Response responseGetTransfers = transactionApiClient.getAccountTransactions(userToken, accountNumber);
        assertThat(responseGetTransfers.statusCode()).isIn(200);
        responseGetTransfers.prettyPrint();
        assertThat(responseGetTransfers.jsonPath().getString("content.amount")).isEqualTo("[" + transferAmount.toString() + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.toAccountNumber")).isEqualTo("[" + accountNumber + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.fromAccountNumber")).isEqualTo("[ATM]");
        assertThat(responseGetTransfers.jsonPath().getString("content.balanceAfterTransaction")).isEqualTo("[" + transferAmount.add(new BigDecimal(balance)).toString() + "]");
    }
}
