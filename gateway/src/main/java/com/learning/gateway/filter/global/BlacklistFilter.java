package com.learning.gateway.filter.global;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class BlacklistFilter implements GlobalFilter, Ordered {

    @Autowired
    private BlacklistResolver blacklistResolver;

    public BlacklistFilter() {
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String userId = headers.getFirst("X-YH-Gateway-SystemUser-Id");
        String clientIp = Utils.getClientIp(exchange.getRequest());
        if (this.blacklistResolver.existBlacklist(clientIp)) {
            throw new UnauthorizedException("此ip目前已经在黑名单中!");
        } else if (null != userId && this.blacklistResolver.existBlacklist(userId)) {
            throw new UnauthorizedException("此用户目前已经在黑名单中!");
        } else {
            return chain.filter(exchange);
        }
    }

    public int getOrder() {
        return -2147483647;
    }
}
