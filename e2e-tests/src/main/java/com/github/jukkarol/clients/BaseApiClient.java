package com.github.jukkarol.clients;

import org.springframework.stereotype.Component;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@Component
public abstract class BaseApiClient {

    protected final RequestSpecification spec;

    protected BaseApiClient(String baseUrl) {
        spec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .build();
    }

    protected RequestSpecification withToken(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(spec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}