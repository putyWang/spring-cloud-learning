package com.learning.config.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
import java.util.UUID;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/3 下午8:58
 */
@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        // 配置HTTP客户端
                        HttpClient.create()
                                .runOn(LoopResources.create("event-loop", 4, true))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 60)
                                .responseTimeout(Duration.ofSeconds(60))
                                .doOnConnected(conn ->
                                        conn.addHandlerLast(new ReadTimeoutHandler(60))
                                                .addHandlerLast(new WriteTimeoutHandler(60))
                                )
                )).filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            String requestId = UUID.randomUUID().toString();
            log.info("Request: {}-{}-{}", requestId, clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> log.info("Request Header: {}={}", name, value))
            );

            // 打印请求体（需处理非重复消费问题）
            if (clientRequest.body() != null) {
                log.info("request body:{}", clientRequest.body());
            }
            return next.exchange(clientRequest)
                    .flatMap(clientResponse -> {
                        // 1. 读取响应体并转换为字符串（限制大小）
                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    // 2. 记录日志
                                    log.info("requestId: {} | Status: {} | Body: {}", requestId, clientResponse.statusCode(), body);
                                    // 3. 重建响应（保持原始数据）
                                    return Mono.just(ClientResponse.from(clientResponse)
                                            .body(body)
                                            .build()
                                    );
                                });
                    });
        };
    }
}
