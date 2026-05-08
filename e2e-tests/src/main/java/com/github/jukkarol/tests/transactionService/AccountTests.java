package com.github.jukkarol.tests.transactionService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountTests {

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private UserDbHelper userDbHelper;

    @Test
    void shouldReturnTokenOnValidCredentials() throws Exception{

    }
}
