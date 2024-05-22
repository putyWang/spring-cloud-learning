package com.learning.gateway.filter.log;

import io.netty.handler.codec.http.HttpScheme;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class ResponseLogFilter implements GlobalFilter, Ordered {
    private static final String WEBSOCKET = "websocket";

    @Value("${spring.application.name}")
    private String projectName;
    @Value("${spring.application.name}")
    private String appName;
    @Value("${gateway.environment}")
    private String appEnv;
    @Value("${logging.save-to-db}")
    private Boolean saveDb;
    @Autowired
    LogUtils logUtils;
    @Value("${spring.cloud.gateway.response.log.filter:false}")
    private Boolean recordResponse;
    @Value("${spring.cloud.gateway.response.log.body.filter:false}")
    private Boolean recordResponseBody;

    public ResponseLogFilter() {
    }

    public int getOrder() {
        return -2;
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (this.recordResponse) {
            try {
                ServerHttpRequest request = exchange.getRequest();
                ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
                URI requestUri = request.getURI();
                String uriQuery = requestUri.getQuery();
                HttpHeaders headers = request.getHeaders();
                MediaType mediaType = headers.getContentType();
                String schema = requestUri.getScheme();
                String method = request.getMethodValue().toUpperCase();
                String upgrade = headers.getUpgrade();
                if (!HttpScheme.HTTP.name().toString().equals(schema) && !HttpScheme.HTTPS.name().toString().equals(schema)) {
                    return chain.filter(exchange);
                } else {
                    AtomicReference<String> requestBody = new AtomicReference();
                    if (Objects.nonNull(mediaType) && LogUtils.isUploadFile(mediaType)) {
                        requestBody.set("upload file");
                        return chain.filter(exchange);
                    } else if (null != upgrade && upgrade.equalsIgnoreCase("websocket")) {
                        return chain.filter(exchange);
                    } else {
                        ServerHttpResponseDecorator decoratedResponse = this.getServerHttpResponseDecorator(exchange);
                        return chain.filter(exchange.mutate().response(decoratedResponse).build());
                    }
                }
            } catch (Exception var14) {
                Exception e = var14;
                log.error("Exception in response log，{}", e.getMessage());
                return chain.filter(exchange);
            }
        } else {
            return chain.filter(exchange);
        }
    }

    private ServerHttpResponseDecorator getServerHttpResponseDecorator(final ServerWebExchange exchange) {
        final ServerHttpResponse originalResponse = exchange.getResponse();
        final DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        final HttpStatus httpStatus = originalResponse.getStatusCode();
        final ServerHttpRequest request = exchange.getRequest();
        URI requestUri = request.getURI();
        String uriQuery = requestUri.getQuery();
        final String url = requestUri.getPath() + (StringUtils.isNotBlank(uriQuery) ? "?" + uriQuery : "");
        final HttpHeaders headers = request.getHeaders();
        final String method = request.getMethodValue().toUpperCase();
        final String requestId = headers.getFirst("X-YH-Gateway-Request-Id");
        return new ServerHttpResponseDecorator(originalResponse) {
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map((dataBuffers) -> {
                        DataBuffer join = bufferFactory.join(dataBuffers);
                        String responseBody = "";
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);
                        if (ResponseLogFilter.this.recordResponseBody) {
                            Charset charset = LogUtils.getMediaTypeCharset(originalResponse.getHeaders().getContentType());
                            responseBody = new String(content, charset);
                        }

                        long handleTime = LogUtils.getHandleTime(headers);
                        LogEntity logEntity = new LogEntity(LogType.RESPONSE);
                        logEntity.setAppName(ResponseLogFilter.this.appName);
                        logEntity.setAppEnv(ResponseLogFilter.this.appEnv);
                        logEntity.setProject(ResponseLogFilter.this.projectName);
                        logEntity.setLevel(LogLevel.INFO);
                        logEntity.setRequestUrl(url);
                        logEntity.setResponseBody(responseBody);
                        logEntity.setRequestMethod(method);
                        if (Objects.nonNull(httpStatus)) {
                            logEntity.setStatus(httpStatus.value());
                        }

                        logEntity.setHandleTime(handleTime);
                        logEntity.setRequestId(requestId);
                        logEntity.setIp(Utils.getClientIp(request));
                        exchange.getSession().subscribe((session) -> {
                            logEntity.setSessionId(session.getId());
                        });
                        ResponseLogFilter.this.logUtils.doRecord(logEntity);
                        return bufferFactory.wrap(content);
                    }));
                } else {
                    return super.writeWith(body);
                }
            }
        };
    }
}
