package com.github.jukkarol.tests.transactionService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import com.github.jukkarol.helpers.transactionService.AccountDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    String email = randomUUIDForUser + "@gmail.com";
    String name = randomUUIDForUser;
    String password = "password!123";

    private String userToken;
    private String accountNumber;
    private String balance;

    @BeforeEach
    void setUp() {
        authApiClient.signup(email, name, password);

        userToken = authApiClient.login(email, password).jsonPath().getString("token");
    }

    @AfterEach
    void tearDown() throws Exception {
        userDbHelper.removeUser(email);
        accountDbHelper.removeAccount(accountNumber);
    }

    @Test
    void shouldCreateAccount() throws Exception{
        //create account
        Response responseCreateAccount = accountApiClient.createAccount(userToken);
        assertThat(responseCreateAccount.statusCode()).isIn(201);
        accountNumber = responseCreateAccount.jsonPath().getString("accountNumber");
        balance = responseCreateAccount.jsonPath().getString("balance").replace("[", "").replace("]", "");

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
    }
}
