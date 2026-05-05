package com.github.jukkarol.tests.authService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthTest {

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private UserDbHelper userDbHelper;

    String randomUUID = UUID.randomUUID().toString().substring(10);
    String email = randomUUID + "@gmail.com";
    String name = randomUUID;
    String password = "password!123";

    @Test
    void shouldReturnTokenOnValidCredentials() throws Exception{
        Response responseSignup = authApiClient.signup(email, name, password);
        assertThat(responseSignup.statusCode()).isIn(200);

        Response responseLogin = authApiClient.login(email, password);

        assertThat(responseLogin.statusCode()).isEqualTo(200);
        assertThat(responseLogin.jsonPath().getString("token")).isNotBlank();

        userDbHelper.removeUser(email);
    }

    @Test
    void shouldCanNotLoginWithInvalidCredentials() throws Exception{
        Response responseSignup = authApiClient.signup(email, name, password);
        assertThat(responseSignup.statusCode()).isIn(200);

        Response responseLogin = authApiClient.login(email, password+"1");

        assertThat(responseLogin.statusCode()).isEqualTo(401);

        userDbHelper.removeUser(email);
    }
}