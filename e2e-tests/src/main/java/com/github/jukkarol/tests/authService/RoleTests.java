package com.github.jukkarol.tests.authService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.authService.RoleApiClient;
import com.github.jukkarol.helpers.authService.RoleDbHelper;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RoleTests {
    @Autowired
    private RoleDbHelper authDbHelper;

    @Autowired
    private UserDbHelper userDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private RoleApiClient roleApiClient;

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

        Response responseAdminLogin = authApiClient.login(emailAdmin, passwordAdmin);
        String adminToken = responseAdminLogin.jsonPath().getString("token");

        Response responseCreateRole = roleApiClient.createRole(adminToken, emailUser, "ATM");
        assertThat(responseCreateRole.statusCode()).isEqualTo(201);

        Response responseUserLogin = authApiClient.login(emailUser, passwordUser);
        String userToken = responseUserLogin.jsonPath().getString("token");
        Response responseGetRoles = roleApiClient.getRolesByToken(userToken);
        assertThat(responseGetRoles.statusCode()).isEqualTo(200);
        assertThat(responseGetRoles.jsonPath().getString("token")).isNotBlank();

        assertThat(responseAdminLogin.statusCode()).isEqualTo(200);
        assertThat(responseAdminLogin.jsonPath().getString("token")).isNotBlank();

        userDbHelper.removeUser(emailUser);
        userDbHelper.removeUser(emailAdmin);
    }

    @Test
    void shouldDeleteUserRole() {

    }
}
