package com.github.jukkarol.tests.transactionService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.clients.transactionService.TransactionApiClient;
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
class TransactionTests {

    @Autowired
    private UserDbHelper userDbHelper;

    @Autowired
    private AccountDbHelper accountDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private TransactionApiClient transactionApiClient;

    String randomUUID1 = UUID.randomUUID().toString().substring(10);
    String email1 = randomUUID1 + "@gmail.com";
    String name1 = randomUUID1;
    String password1 = "password!123";

    String randomUUID2 = UUID.randomUUID().toString().substring(10);
    String email2 = randomUUID2 + "@gmail.com";
    String name2 = randomUUID2;
    String password2 = "password!123";

    private String userToken1, userToken2;
    private String accountNumber1, accountNumber2;
    private String balance1, balance2;

    BigDecimal transferAmount = new BigDecimal("100.11");

    @BeforeEach
    void setUp() {
        authApiClient.signup(email1, name1, password2);
        authApiClient.signup(email2, name2, password2);

        userToken1 = authApiClient.login(email1, password1).jsonPath().getString("token");
        userToken2 = authApiClient.login(email2, password2).jsonPath().getString("token");

        var acc1 = accountApiClient.createAccount(userToken1);
        accountNumber1 = acc1.jsonPath().getString("accountNumber");
        balance1 = acc1.jsonPath().getString("balance");

        var acc2 = accountApiClient.createAccount(userToken2);
        accountNumber2 = acc2.jsonPath().getString("accountNumber");
        balance2 = acc2.jsonPath().getString("balance");
    }

    @AfterEach
    void tearDown() throws Exception {
        userDbHelper.removeUser(email1);
        userDbHelper.removeUser(email2);
        accountDbHelper.removeAccount(accountNumber1);
        accountDbHelper.removeAccount(accountNumber2);
    }

    @Test
    void shouldMakeTransfer() throws Exception{
        //make transfer
        Response responseMakeTransfer = transactionApiClient.makeTransfer(userToken1, accountNumber1, accountNumber2, transferAmount);
        assertThat(responseMakeTransfer.statusCode()).isIn(201);
        assertThat(responseMakeTransfer.jsonPath().getString("amount")).isEqualTo(transferAmount.toString());

        //get all user transfers
        Response responseGetTransfers = transactionApiClient.getAccountTransactions(userToken1, accountNumber1);
        assertThat(responseGetTransfers.statusCode()).isIn(200);
        assertThat(responseGetTransfers.jsonPath().getString("content.amount")).isEqualTo("[" + transferAmount.toString() + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.fromAccountNumber")).isEqualTo("[" + accountNumber1 + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.toAccountNumber")).isEqualTo("[" + accountNumber2 + "]");
    }

    @Test
    void shouldMakeTransferForAccountWithoutOwningIt() throws Exception{
        //make transfer
        Response responseMakeTransfer = transactionApiClient.makeTransfer(userToken1, accountNumber2, accountNumber1, transferAmount);
        assertThat(responseMakeTransfer.statusCode()).isIn(403);
        assertThat(responseMakeTransfer.jsonPath().getString("error")).isEqualTo("Permission Denied.");

        //get all user transfers
        Response responseGetTransfers = transactionApiClient.getAccountTransactions(userToken1, accountNumber1);
        assertThat(responseGetTransfers.statusCode()).isIn(200);
        assertThat(responseGetTransfers.jsonPath().getString("content.amount")).isEqualTo("[]");
        assertThat(responseGetTransfers.jsonPath().getString("content.fromAccountNumber")).isEqualTo("[]");
        assertThat(responseGetTransfers.jsonPath().getString("content.toAccountNumber")).isEqualTo("[]");
    }

    @Test
    void shouldMakeTransferWithInsufficientFounds() throws Exception{
        //make transfer
        Response responseMakeTransfer = transactionApiClient.makeTransfer(userToken1, accountNumber1, accountNumber2, transferAmount.add(new BigDecimal(balance1)));
        assertThat(responseMakeTransfer.statusCode()).isIn(400);
        assertThat(responseMakeTransfer.jsonPath().getString("error")).isEqualTo("Not enough funds.");

        //get all user transfers
        Response responseGetTransfers = transactionApiClient.getAccountTransactions(userToken1, accountNumber1);
        assertThat(responseGetTransfers.statusCode()).isIn(200);
        assertThat(responseGetTransfers.jsonPath().getString("content.amount")).isEqualTo("[]");
        assertThat(responseGetTransfers.jsonPath().getString("content.fromAccountNumber")).isEqualTo("[]");
        assertThat(responseGetTransfers.jsonPath().getString("content.toAccountNumber")).isEqualTo("[]");
    }
}
