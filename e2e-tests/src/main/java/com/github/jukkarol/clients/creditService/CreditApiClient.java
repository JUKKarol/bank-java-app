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

    public Response createCredit(String token, String AccountNumber, BigDecimal amountTotal, BigDecimal amountMonthly) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("accountNumber", AccountNumber, "amountTotal", amountTotal, "amountMonthly", amountMonthly))
                .when()
                .post("/credits")
                .then()
                .log().ifError()
                .extract().response();
    }
}