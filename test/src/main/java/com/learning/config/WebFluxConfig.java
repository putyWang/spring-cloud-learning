package com.learning.config;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/3 下午10:21
 */
@Configuration
public class WebFluxConfig {

    @Bean
    public RouterFunction<ServerResponse> dynamicRouter(KieContainer kieContainer, WebClient webClient) {
        // 创建全局处理器
        GlobalHandler globalHandler = new GlobalHandler(kieContainer, webClient);
        return RouterFunctions.route()
                //  全局只支持处理 POST 方法
                .POST("/**", globalHandler::handleRequest)
                .build();
    }

    @RequiredArgsConstructor
    public static class GlobalHandler {
        // 使用KieSession池
        private final KieContainer kieContainer; // Drools 规则容器
        private final WebClient webClient; // Drools 规则容器


        public Mono<ServerResponse> handleRequest(ServerRequest request) {
            // 1. 缓存请求体（解决重复消费）
            Mono<byte[]> bodyBytes = request.bodyToMono(DataBuffer.class)
                    .map(buffer -> {
                        byte[] bytes = new byte[buffer.readableByteCount()];
                        buffer.read(bytes);
                        DataBufferUtils.release(buffer);
                        return bytes;
                    });

            return bodyBytes.flatMap(
                    bytes -> {
                        // 2. 解析参数
                        Map<String, Object> params = JSON.parseObject(new String(bytes));
                        // 1 保存 url
                        params.put("url", request.uri().toString());
                        // 2. 执行 Drools 规则
                        KieSession kieSession = kieContainer.newKieSession();
                        kieSession.insert(params); // 注入参数到规则引擎
                        kieSession.fireAllRules();
                        // 3. 获取规则计算的目标地址
                        String targetUrls = (String) kieSession.getGlobal("targetUrl");
                        kieSession.dispose();
                        // 4
                        List<Mono<String>> responseList = new ArrayList<>();
                        for (String targetUrl : targetUrls.split(",")) {
                            responseList.add(
                                    webClient.post()
                                            .uri(targetUrl).headers(headers -> headers.addAll(request.headers().asHttpHeaders()))
                                            .body(BodyInserters.fromDataBuffers(request.bodyToFlux(DataBuffer.class)))
                                            .retrieve().bodyToMono(String.class)
                            );
                        }
                        return Flux.merge(responseList).collectList()
                                .flatMap(
                                        array ->
                                                ServerResponse.ok().body(String.format(",", array), String.class)
                                );
                    }
            );
        }
    }
}
