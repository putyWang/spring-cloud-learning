package com.learning.gateway.filter.log;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Autowired
    LogUtils logUtils;
    @Value("${spring.application.name}")
    private String projectName;
    @Value("${spring.application.name}")
    private String appName;
    @Value("${gateway.environment}")
    private String appEnv;
    @Value("${logging.save-to-db}")
    private Boolean saveDb;
    @Value("${spring.cloud.gateway.request.log.filter:false}")
    private Boolean recordRequest;
    @Value("${spring.cloud.gateway.request.log.body.filter:false}")
    private Boolean recordRequestBody;

    public RequestLogFilter() {
    }

    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (this.recordRequest) {
            long startTime = System.currentTimeMillis();

            try {
                ServerHttpRequest request = exchange.getRequest();
                AtomicReference<String> requestId = new AtomicReference(Utils.requestIdWithUuid());
                Consumer<HttpHeaders> httpHeadersConsumer = (httpHeaders) -> {
                    String headerRequestId = request.getHeaders().getFirst("X-YH-Gateway-Request-Id");
                    if (StringUtils.isBlank(headerRequestId)) {
                        httpHeaders.set("X-YH-Gateway-Request-Id", (String)requestId.get());
                    } else {
                        requestId.set(headerRequestId);
                    }

                    httpHeaders.set("X-YH-Gateway-Request-Start", String.valueOf(startTime));
                };
                String uriQuery = request.getURI().getQuery();
                String url = request.getURI().getPath() + (StringUtils.isNotBlank(uriQuery) ? "?" + uriQuery : "");
                MediaType mediaType = request.getHeaders().getContentType();
                String method = request.getMethodValue().toUpperCase();
                AtomicReference<String> requestBody = new AtomicReference();
                if (this.recordRequestBody) {
                    AtomicBoolean newBody = new AtomicBoolean(false);
                    if (Objects.nonNull(mediaType) && LogUtils.isUploadFile(mediaType)) {
                        requestBody.set("upload file");
                    } else if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
                        if (StringUtils.isNotBlank(uriQuery)) {
                            requestBody.set(uriQuery);
                        }
                    } else {
                        newBody.set(true);
                    }
                }

                LogEntity logEntity = new LogEntity();
                logEntity.setLevel(LogLevel.INFO);
                logEntity.setRequestUrl(url);
                logEntity.setRequestBody((String)requestBody.get());
                logEntity.setRequestMethod(method);
                logEntity.setRequestId((String)requestId.get());
                logEntity.setIp(Utils.getClientIp(request));
                logEntity.setAppName(this.appName);
                logEntity.setAppEnv(this.appEnv);
                logEntity.setProject(this.projectName);
                ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeadersConsumer).build();
                ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
                return chain.filter(build).then(this.logUtils.doRecord(logEntity));
            } catch (Exception var16) {
                Exception e = var16;
                log.error("Exception in request log，{}", e);
                return chain.filter(exchange);
            }
        } else {
            return chain.filter(exchange);
        }
    }
}
