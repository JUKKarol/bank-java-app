package com.github.jukkarol.clients.creditService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class CreditApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public CreditApiClient(TestConfig config) {
        super(config.getCreditService().getUrl());
        this.myConfig = config;
    }

    public Response createCredit(String token, String accountNumber, BigDecimal amountTotal, BigDecimal amountMonthly) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("accountNumber", accountNumber, "amountTotal", amountTotal, "amountMonthly", amountMonthly))
                .when()
                .post("/credits")
                .then()
                .log().ifError()
                .extract().response();
    }

    public Response getCreditsByAccountNumber(String token, String accountNumber) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/credits/" + accountNumber)
                .then()
                .log().ifError()
                .extract().response();
    }
}