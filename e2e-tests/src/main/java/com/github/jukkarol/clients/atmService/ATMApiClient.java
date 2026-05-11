package com.github.jukkarol.clients.atmService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class ATMApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public ATMApiClient(TestConfig config) {
        super(config.getAtmService().getUrl());
        this.myConfig = config;
    }

    public Response makeWithdrawal(String token, String AccountNumber, BigDecimal amount) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("accountNumber", AccountNumber, "amount", amount))
                .when()
                .post("/withdrawals")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response makeDeposit(String token, String AccountNumber, BigDecimal amount) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("accountNumber", AccountNumber, "amount", amount))
                .when()
                .post("/deposits")
                .then()
                .log().ifError()
                .extract().response();
    }
}