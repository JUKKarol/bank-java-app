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
        Response response = authApiClient.login("admin", "password");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("token")).isNotBlank();
    }
}