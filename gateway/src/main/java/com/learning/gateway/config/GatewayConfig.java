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


    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {

        // 2.在请求头中添加鉴权码
        return builder.routes()
                .route(r -> r.path(loginUrl)
                        .filters(f -> f.filter(new LoginFilter()))
                        .uri("http://auther-server/login")
                        .order(0)
                        .id("login_router")
                )
                .build();
    }
}
