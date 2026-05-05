package com.github.jukkarol.clients.authService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class AuthApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public AuthApiClient(TestConfig config) {
        super(config.getAuthService().getUrl());
        this.myConfig = config;
    }

    public Response login(String email, String password) {
        return given(spec)
                .body(Map.of("email", email, "password", password))
                .when()
                .post("/login")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response signup(String email, String name, String password) {
        return given(spec)
                .body(Map.of("email", email, "name", name, "password", password))
                .when()
                .post("/signup")
                .then()
                .log().ifError()
                .extract().response();
    }
}