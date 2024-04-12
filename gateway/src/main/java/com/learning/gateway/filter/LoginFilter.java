package com.learning.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 登陆过滤器
 */
public class LoginFilter implements GatewayFilter, Ordered {

    @Value("${gate.login.oauth2.client.id:learning}")
    private String clientId = "learning";

    @Value("${gate.login.oauth2.client.secret:learning}")
    private String clientSecret = "learning";

    private static final String AUTHORIZATION_START = "Basic ";

    /**
     * 登陆
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1.加密clientId以及secret
        String decodeSecret = clientId + ":" + clientSecret;
        byte[] encode = Base64.getEncoder().encode(decodeSecret.getBytes(StandardCharsets.UTF_8));

        ServerHttpRequest request = exchange.getRequest();

        request = request.mutate().headers(httpHeader -> {
            httpHeader.add(HttpHeaders.AUTHORIZATION, AUTHORIZATION_START + encode);
        }).build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}