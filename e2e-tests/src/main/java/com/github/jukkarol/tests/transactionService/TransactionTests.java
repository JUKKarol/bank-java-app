package com.github.jukkarol.tests.transactionService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.clients.transactionService.TransactionApiClient;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import com.github.jukkarol.helpers.transactionService.AccountDbHelper;
import io.restassured.response.Response;
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

    String randomUUIDForUser1 = UUID.randomUUID().toString().substring(10);
    String emailUser1 = randomUUIDForUser1 + "@gmail.com";
    String nameUser1 = randomUUIDForUser1;
    String passwordUser1 = "password!123";

    String randomUUIDForUser2 = UUID.randomUUID().toString().substring(10);
    String emailUser2 = randomUUIDForUser2 + "@gmail.com";
    String nameUser2 = randomUUIDForUser2;
    String passwordUser2 = "password!123";

    BigDecimal transferAmount = new BigDecimal("100.11");

    @Test
    void shouldMakeTransfer() throws Exception{
        //create users
        Response responseUserSignup1 = authApiClient.signup(emailUser1, nameUser1, passwordUser1);
        assertThat(responseUserSignup1.statusCode()).isIn(200);

        Response responseUserSignup2 = authApiClient.signup(emailUser2, nameUser2, passwordUser2);
        assertThat(responseUserSignup2.statusCode()).isIn(200);

        //get tokens
        Response responseUserLogin1 = authApiClient.login(emailUser1, passwordUser1);
        assertThat(responseUserLogin1.statusCode()).isIn(200);
        String userToken1 = responseUserLogin1.jsonPath().getString("token");

        Response responseUserLogin2 = authApiClient.login(emailUser2, passwordUser2);
        assertThat(responseUserLogin2.statusCode()).isIn(200);
        String userToken2 = responseUserLogin2.jsonPath().getString("token");

        //create accounts
        Response responseCreateAccount1 = accountApiClient.createAccount(userToken1);
        assertThat(responseCreateAccount1.statusCode()).isIn(201);
        String accountNumber1 = responseCreateAccount1.jsonPath().getString("accountNumber");
        String balance1 = responseCreateAccount1.jsonPath().getString("balance");

        Response responseCreateAccount2 = accountApiClient.createAccount(userToken2);
        assertThat(responseCreateAccount2.statusCode()).isIn(201);
        String accountNumber2 = responseCreateAccount2.jsonPath().getString("accountNumber");
        String balance2 = responseCreateAccount2.jsonPath().getString("balance");

        //make transfer
        Response responseMakeTransfer = transactionApiClient.makeTransfer(userToken1, accountNumber1, accountNumber2, transferAmount);
        assertThat(responseMakeTransfer.statusCode()).isIn(201);
        assertThat(responseMakeTransfer.jsonPath().getString("amount")).isEqualTo(transferAmount.toString());
        responseMakeTransfer.prettyPrint();

        //get all user transfers
        Response responseGetTransfers = transactionApiClient.getAccountTransactions(userToken1, accountNumber1);
        assertThat(responseGetTransfers.statusCode()).isIn(200);
        assertThat(responseGetTransfers.jsonPath().getString("content.amount")).isEqualTo("[" + transferAmount.toString() + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.fromAccountNumber")).isEqualTo("[" + accountNumber1 + "]");
        assertThat(responseGetTransfers.jsonPath().getString("content.toAccountNumber")).isEqualTo("[" + accountNumber2 + "]");
        responseGetTransfers.prettyPrint();

        //delete created users and accounts
        userDbHelper.removeUser(emailUser1);
        userDbHelper.removeUser(emailUser2);
        accountDbHelper.removeAccount(accountNumber1);
        accountDbHelper.removeAccount(accountNumber2);
    }
}
