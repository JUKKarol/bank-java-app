package com.github.jukkarol.clients.creditService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class CreditHistoryApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public CreditHistoryApiClient(TestConfig config) {
        super(config.getCreditService().getUrl());
        this.myConfig = config;
    }

    public Response getCreditHistoryByCreditId(String token, Long creditId) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/credits/history/" + creditId)
                .then()
                .log().ifError()
                .extract().response();
    }
}