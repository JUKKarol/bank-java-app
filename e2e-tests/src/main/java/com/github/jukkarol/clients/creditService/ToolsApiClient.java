package com.github.jukkarol.clients.creditService;

import com.github.jukkarol.clients.BaseApiClient;
import com.github.jukkarol.config.TestConfig;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Component
public class ToolsApiClient extends BaseApiClient {

    private final TestConfig myConfig;

    public ToolsApiClient(TestConfig config) {
        super(config.getCreditService().getUrl());
        this.myConfig = config;
    }

    public Response processSpecifiedCreditsInstallments(String token, List<Long> ids) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("ids", ids))
                .when()
                .post("/tools")
                .then()
                .log().ifError()
                .extract().response();
    }
}