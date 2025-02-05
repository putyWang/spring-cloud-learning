package com.learning.gateway.utils;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseUtils {
    public ResponseUtils() {
    }

    public static void addResponseHeader(ServerHttpResponse response, String headerName, String headerValue) {
        response.getHeaders().add(headerName, headerValue);
    }

    public static Mono<Void> setReturnFORBIDDEN(ServerHttpResponse originalResponse, Object returnString) {
        originalResponse.setStatusCode(HttpStatus.FORBIDDEN);
        return setReturnNoStatus(originalResponse, returnString);
    }

    private static Mono<Void> setReturnNoStatus(ServerHttpResponse originalResponse, Object returnString) {
        DataBuffer buffer = originalResponse.bufferFactory().wrap(JSON.toJSONString(returnString).getBytes(StandardCharsets.UTF_8));
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return originalResponse.writeWith(Flux.just(buffer));
    }

    public static Mono<Void> setReturnUNAUTHORIZED(ServerHttpResponse originalResponse, Object returnString) {
        originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        return setReturnNoStatus(originalResponse, returnString);
    }

    public static Mono<Void> setReturnException(HttpStatus status, ServerHttpResponse originalResponse, Object returnString) {
        originalResponse.setStatusCode(status);
        return setReturnNoStatus(originalResponse, returnString);
    }
}