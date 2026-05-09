package com.github.jukkarol.clients.transactionService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class TransactionApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public TransactionApiClient(TestConfig config) {
        super(config.getTransactionService().getUrl());
        this.myConfig = config;
    }

    public Response createAccount(String token) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/accounts")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response getAllAccounts(String token) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response getAccount(String token, String accountNumber) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountNumber)
                .then()
                .log().ifError()
                .extract().response();
    }
}