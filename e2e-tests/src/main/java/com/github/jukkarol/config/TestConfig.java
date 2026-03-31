package com.github.jukkarol.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties
public class TestConfig {

    private String urlGateway;

    private Service authService;
    private Service transactionService;
    private Service atmService;

    @Getter
    @Setter
    public static class Service {
        private String url;
        private Datasource datasource;
    }

    @Getter
    @Setter
    public static class Datasource {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }
}