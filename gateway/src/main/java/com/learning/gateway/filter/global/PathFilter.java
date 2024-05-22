package com.learning.gateway.filter.global;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log4j2
public class PathFilter implements GlobalFilter, Ordered {

    @Autowired
    @Qualifier("redis-cache-service")
    private CacheService memoryCacheService;

    public PathFilter() {
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        boolean hasKey = this.memoryCacheService.hasKey("service_apis", path);
        if (hasKey) {
            return chain.filter(exchange);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "请配置接口列表");
        }
    }

    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
