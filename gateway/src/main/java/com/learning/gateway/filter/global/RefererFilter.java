package com.learning.gateway.filter.global;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class RefererFilter implements GlobalFilter, Ordered {

    private static final String HTTPS_START = "https://";
    private static final String HTTP_START = "http://";
    private static final String LINE = "/";
    private static final String COLON = ":";

    @Value("${yanhua.referer.enable}")
    private boolean refererEnable;
    @Value("${yanhua.referer.list}")
    private String refererList;
    @Autowired
    private BlacklistService blacklistService;
    private Set<String> refererHostSet = new HashSet();

    public RefererFilter() {
    }

    @PostConstruct
    private void initReferers() {
        if (null != this.refererList && !this.refererList.isEmpty()) {
            Stream<String> refererStream = Arrays.stream(this.refererList.split("\\,")).filter((sp) -> {
                return !sp.trim().isEmpty();
            }).map((u) -> {
                try {
                    String host = (new URI(u)).getHost();
                    log.info("referer host:{}, original:{}", host, u);
                    return host;
                } catch (URISyntaxException var2) {
                    URISyntaxException e = var2;
                    log.error("config referer error:{}", e.getMessage());
                    return null;
                }
            }).filter(Objects::nonNull);
            Set<String> collect = (Set)refererStream.collect(Collectors.toSet());
            this.refererHostSet.addAll(collect);
        }

    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!this.refererEnable) {
            return chain.filter(exchange);
        } else {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            String referer = headers.getFirst("Referer");
            String host;
            if (StringUtils.isBlank(referer)) {
                this.blacklistService.recordErrorAccess(exchange);
                host = String.format("请提供有效的 Referer或请检查 Apollo 配置", referer);
                return this.commonError(exchange, host);
            } else {
                try {
                    host = (new URI(referer)).getHost();
                    if (!this.refererHostSet.contains(host)) {
                        String msg = String.format("Referer 错误:%s, 请检查 Apollo 配置", referer);
                        return this.commonError(exchange, msg);
                    } else {
                        return chain.filter(exchange);
                    }
                } catch (URISyntaxException var8) {
                    URISyntaxException e = var8;
                    log.error(e.getMessage());
                    this.blacklistService.recordErrorAccess(exchange);
                    host = String.format("Referer 错误:%s, 请提供有效的 Referer或请检查 Apollo 配置", referer);
                    return this.commonError(exchange, host);
                }
            }
        }
    }

    public int getOrder() {
        return -2147483646;
    }

    private boolean isValid(String referer) {
        return false;
    }

    private Mono<Void> commonError(ServerWebExchange exchange, String msg) {
        return ResponseUtils.setReturnFORBIDDEN(exchange.getResponse(), PublicResult.failed(msg, msg));
    }
}
