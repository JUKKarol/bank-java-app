package com.github.jukkarol.clients;

import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class TransactionApiClient extends BaseApiClient {

    public TransactionApiClient(TestConfig config) {
        super(config.getTransactionService().getUrl());
    }

    public Response getAccounts(String token) {
        return given(withToken(token))
                .when()
                .get("/accounts")
                .then()
                .extract().response();
    }
}
