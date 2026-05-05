package com.github.jukkarol.clients.authService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class RoleApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public RoleApiClient(TestConfig config) {
        super(config.getAuthService().getUrl());
        this.myConfig = config;
    }

    public Response createRole(String token, String userEmail, String role) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("userEmail", userEmail, "role", role))
                .when()
                .post("/roles")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response deleteRole(String token, String userEmail, String role) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("userEmail", userEmail, "role", role))
                .when()
                .delete("/roles")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response getRolesByToken(String token) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/roles")
                .then()
                .log().ifError()
                .extract().response();
    }
}