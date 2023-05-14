package com.learning.gateway.config;

import com.learning.gateway.filter.LoginFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class GatewayConfig {

    @Value("${gate.login.url:/login}")
    private String loginUrl = "/login";

    @Value("${gate.login.oauth2.client.id:learning}")
    private String clientId = "learning";

    @Value("${gate.login.oauth2.client.secret:learning}")
    private String clientSecret = "learning";

    private static final String AUTHORIZATION_START = "Basic ";


    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {

        // 1.加密clientId以及secret
        String decodeSecret = clientId + ":" + clientSecret;
        byte[] encode = Base64.getEncoder().encode(decodeSecret.getBytes(StandardCharsets.UTF_8));

        // 2.在请求头中添加鉴权码
        return builder.routes()
                .route(r -> r.path(loginUrl)
                        .filters(f -> f.filter(new LoginFilter())
                                .addRequestHeader(HttpHeaders.AUTHORIZATION, AUTHORIZATION_START + encode))
                        .uri("http://auther-server/login")
                        .order(0)
                        .id("login_router")
                )
                .build();
    }
}
