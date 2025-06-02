package com.learning.config.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/26 下午9:41
 */
@Log4j2
public class LogFilter {

    private static Map<String, InterfaceAccessLogPo> reqParamCache = new ConcurrentHashMap<>();

    /**
     * 请求日志处理
     * @param request 请求对象
     * @param next 下一处理
     * @return 处理结果
     */
    public Mono<ClientResponse> reqFilter (ClientRequest request, ExchangeFunction next) {
        return Mono.deferContextual(ctx -> {
            String requestId = ctx.getOrDefault("requestId", "default");
            log.info("Starting request ID: {}", requestId);
            return next.exchange(request)
                    .flatMap(response ->
                            Mono.fromRunnable(() ->
                                    log.info("Ending request ID {}: Status {}", requestId, response.statusCode())
                            ).thenReturn(response)
                    );
        });
    }
}
