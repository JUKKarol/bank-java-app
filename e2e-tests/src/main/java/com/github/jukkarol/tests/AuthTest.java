package com.github.jukkarol.tests;

import com.github.jukkarol.clients.AuthApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthTest {

    @Autowired
    private AuthApiClient authApiClient;

    @Test
    void shouldReturnTokenOnValidCredentials() {
        Response responseSignup = authApiClient.signup("user@gmail.com", "user", "password");
        assertThat(responseSignup.statusCode()).isIn(200, 409);

        Response responseLogin = authApiClient.login("user@gmail.com", "password");

        assertThat(responseLogin.statusCode()).isEqualTo(200);
        assertThat(responseLogin.jsonPath().getString("token")).isNotBlank();
    }
}