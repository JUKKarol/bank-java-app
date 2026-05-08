package com.github.jukkarol.tests.transactionService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TransactionTests {

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private UserDbHelper userDbHelper;

    @Test
    void shouldReturnTokenOnValidCredentials() throws Exception{

    }
}
