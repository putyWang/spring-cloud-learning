package com.learning.gateway.filter;

import com.learning.core.domain.constants.Constant;
import com.learning.core.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 为对应服务添加对应header 过滤器
 */
@Component
@Slf4j
public class AppAuthedFilter implements GlobalFilter, Ordered {

    @Value("${spring.app.id:learning}")
    private String appId;


    @Value("${spring.app.secret:learning}")
    private String secret;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1.对认证请求头进行加密
        String authedString = "app:" + secret + "-" + appId;
        String encode = MD5Utils.encode(authedString);

        ServerHttpRequest request = exchange.getRequest();

        request = request.mutate().headers(httpHeader -> {
            httpHeader.add(Constant.APP_SECRET_HEADER, encode);
        }).build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
