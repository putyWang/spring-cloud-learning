package com.learning.gateway.filter.factory;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomRateLimiterGatewayFilterFactory
        extends AbstractGatewayFilterFactory<CustomRateLimiterGatewayFilterFactory.Config>
        implements Ordered {

    public static final String KEY_RESOLVER_KEY = "keyResolver";
    private final RateLimiter defaultRateLimiter;
    private final KeyResolver defaultKeyResolver;

    public CustomRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter, KeyResolver defaultKeyResolver) {
        super(Config.class);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    public GatewayFilter apply(Config config) {
        KeyResolver resolver = config.keyResolver == null ? this.defaultKeyResolver : config.keyResolver;
        RateLimiter<Object> limiter = config.rateLimiter == null ? this.defaultRateLimiter : config.rateLimiter;
        return (exchange, chain) -> {
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            return resolver.resolve(exchange).flatMap((key) ->
                limiter.isAllowed(route.getId(), key).flatMap((response) -> {
                    if (response.isAllowed()) {
                        return chain.filter(exchange);
                    } else {
                        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "访问过快");
                    }
                })
            ).onErrorResume((throwable) -> {
                if (throwable instanceof ResponseStatusException) {
                    throw (ResponseStatusException)throwable;
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getMessage());
                }
            }).onErrorResume((throwable) -> {
                if (throwable instanceof ResponseStatusException) {
                    throw (ResponseStatusException)throwable;
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getMessage());
                }
            });
        };
    }

    public int getOrder() {
        return -2147483642;
    }

    public RateLimiter getDefaultRateLimiter() {
        return this.defaultRateLimiter;
    }

    public KeyResolver getDefaultKeyResolver() {
        return this.defaultKeyResolver;
    }

    @Data
    public static class Config {
        private KeyResolver keyResolver;
        private RateLimiter rateLimiter;
        private HttpStatus statusCode;

        public Config() {
            this.statusCode = HttpStatus.TOO_MANY_REQUESTS;
        }
    }
}
