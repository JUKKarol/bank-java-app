package com.github.jukkarol.tests.authService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.authService.RoleApiClient;
import com.github.jukkarol.helpers.authService.RoleDbHelper;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
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

    String userToken, adminToken;

    @BeforeEach
    void setUp() throws Exception {
        authApiClient.signup(emailUser, nameUser, passwordUser);
        authApiClient.signup(emailAdmin, nameAdmin, passwordAdmin);

        authDbHelper.addRoleToUser(emailAdmin, "ADMIN");

        userToken = authApiClient.login(emailUser, passwordUser).jsonPath().getString("token");
        adminToken = authApiClient.login(emailAdmin, passwordAdmin).jsonPath().getString("token");
    }

    @AfterEach
    void tearDown() throws Exception {
        userDbHelper.removeUser(emailUser);
        userDbHelper.removeUser(emailAdmin);
    }

    @Test
    void shouldAddUserNewRole() throws Exception{
        Response responseCreateRole = roleApiClient.createRole(adminToken, emailUser, "ATM");
        assertThat(responseCreateRole.statusCode()).isEqualTo(201);

        Response responseGetRoles = roleApiClient.getRolesByToken(userToken);
        assertThat(responseGetRoles.statusCode()).isEqualTo(200);
        assertThat(responseGetRoles.jsonPath().getString("roles")).isEqualTo("[ROLE_ATM, ROLE_USER]");
    }

    @Test
    void shouldDeleteUserRole() throws Exception{
        authDbHelper.addRoleToUser(emailUser, "ROLE_ATM");
        userToken = authApiClient.login(emailUser, passwordUser).jsonPath().getString("token");

        Response responseDeleteRole = roleApiClient.deleteRole(adminToken, emailUser, "ROLE_ATM");
        assertThat(responseDeleteRole.statusCode()).isEqualTo(200);
        assertThat(responseDeleteRole.jsonPath().getString("roles")).isEqualTo("[ROLE_USER]");

        Response responseGetRoles = roleApiClient.getRolesByToken(userToken);
        assertThat(responseGetRoles.statusCode()).isEqualTo(200);
        assertThat(responseGetRoles.jsonPath().getString("roles")).isEqualTo("[ROLE_USER]");
    }

    //ToDo: add test where user without admin will try to create role
}
