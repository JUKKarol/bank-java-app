package com.github.jukkarol.tests.authService;

import com.github.jukkarol.clients.AuthApiClient;
import com.github.jukkarol.helpers.AuthDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RoleTests {
    @Autowired
    private AuthDbHelper authDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    String randomUUIDForUser = UUID.randomUUID().toString().substring(10);
    String emailUser = randomUUIDForUser + "@gmail.com";
    String nameUser = randomUUIDForUser;
    String passwordUser = "password!123";

    String randomUUIDForAdmin = UUID.randomUUID().toString().substring(10);
    String emailAdmin = randomUUIDForAdmin + "@gmail.com";
    String nameAdmin = randomUUIDForAdmin;
    String passwordAdmin = "password!123";

    @Test
    void shouldAddUserNewRole() throws Exception{
        Response responseUserSignup = authApiClient.signup(emailUser, nameUser, passwordUser);
        assertThat(responseUserSignup.statusCode()).isIn(200);

        Response responseAdminSignup = authApiClient.signup(emailAdmin, nameAdmin, passwordAdmin);
        assertThat(responseAdminSignup.statusCode()).isIn(200);

        authDbHelper.addRoleToUser(emailAdmin, "ADMIN");

        Response responseLogin = authApiClient.login(emailAdmin, passwordAdmin);

        //add role ATM form admin to user via api
        //chek is role changed

        assertThat(responseLogin.statusCode()).isEqualTo(200);
        assertThat(responseLogin.jsonPath().getString("token")).isNotBlank();

        //remove users
    }

    @Test
    void shouldDeleteUserRole() {

    }
}
