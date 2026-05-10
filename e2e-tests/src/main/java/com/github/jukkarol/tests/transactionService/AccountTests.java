package com.github.jukkarol.tests.transactionService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import com.github.jukkarol.helpers.transactionService.AccountDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountTests {

    @Autowired
    private UserDbHelper userDbHelper;

    @Autowired
    private AccountDbHelper accountDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private AccountApiClient accountApiClient;

    String randomUUIDForUser = UUID.randomUUID().toString().substring(10);
    String emailUser = randomUUIDForUser + "@gmail.com";
    String nameUser = randomUUIDForUser;
    String passwordUser = "password!123";

    @Test
    void shouldCreateAccount() throws Exception{
        //create user
        Response responseUserSignup = authApiClient.signup(emailUser, nameUser, passwordUser);
        assertThat(responseUserSignup.statusCode()).isIn(200);

        //get token
        Response responseUserLogin = authApiClient.login(emailUser, passwordUser);
        assertThat(responseUserLogin.statusCode()).isIn(200);
        String userToken = responseUserLogin.jsonPath().getString("token");

        //create account
        Response responseCreateAccount = accountApiClient.createAccount(userToken);
        assertThat(responseCreateAccount.statusCode()).isIn(201);
        String accountNumber = responseCreateAccount.jsonPath().getString("accountNumber");
        String balance = responseCreateAccount.jsonPath().getString("balance").replace("[", "").replace("]", "");

        // get all account
        Response responseGetAccounts = accountApiClient.getAllAccounts(userToken);
        assertThat(responseGetAccounts.statusCode()).isIn(200);
        assertThat(responseGetAccounts.jsonPath().getString("accounts.accountNumber")).isEqualTo("[" + accountNumber + "]");
        assertThat(responseGetAccounts.jsonPath().getString("accounts.balance")).isEqualTo("[" + balance + "]");

        // get account
        Response responseGetAccount = accountApiClient.getAccount(userToken, accountNumber);
        assertThat(responseGetAccount.statusCode()).isIn(200);
        assertThat(responseGetAccount.jsonPath().getString("accountNumber")).isEqualTo(accountNumber);
        assertThat(responseGetAccount.jsonPath().getString("balance")).isEqualTo(balance);
        assertThat(responseGetAccount.jsonPath().getString("userId")).isNotBlank();
        assertThat(responseGetAccount.jsonPath().getString("createdAt")).isNotBlank();
        assertThat(responseGetAccount.jsonPath().getString("updatedAt")).isNotBlank();

        //delete created user and account
        userDbHelper.removeUser(emailUser);
        accountDbHelper.removeAccount(accountNumber);
    }
}
