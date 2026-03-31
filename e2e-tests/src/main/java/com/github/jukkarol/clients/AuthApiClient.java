package com.github.jukkarol.clients;

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

    public Response login(String username, String password) {
        return given(spec)
                .body(Map.of("username", username, "password", password))
                .when()
                .post("/login")
                .then()
                .log().ifError()
                .extract().response();
    }
}