package com.learning.gateway.filter.factory;

import com.learning.gateway.service.CacheService;
import com.learning.gateway.utils.ResponseUtils;
import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesFactory;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.GatewayToStringStyler;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnBean({InitCache.class})
@DependsOn({"initCache"})
@Log4j2
public class CustomTimeOutGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomTimeOutGatewayFilterFactory.Config> implements Ordered {
    @Value("${spring.cloud.hystrix.default-timeout}")
    private int defaultTimeout;

    @Value("${spring.cloud.hystrix.default-semaphore}")
    private int defaultSemaphore;

    @Autowired
    @Qualifier("redis-cache-service")
    private CacheService cacheService;

    private final ObjectProvider<DispatcherHandler> dispatcherHandler;

    public CustomTimeOutGatewayFilterFactory(ObjectProvider<DispatcherHandler> dispatcherHandler) {
        super(Config.class);
        this.dispatcherHandler = dispatcherHandler;
    }

    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("name");
    }

    public GatewayFilter apply(final Config config) {
        return new GatewayFilter() {
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return Mono.subscriberContext().flatMap((context) -> {
                    ServerHttpRequest request = exchange.getRequest();
                    String path = request.getPath().pathWithinApplication().value();
                    int timeout = CustomTimeOutGatewayFilterFactory.this.defaultTimeout;
                    String cacheTimeOut = CustomTimeOutGatewayFilterFactory.this.cacheService.getFromCache("service_api_timeout", path);
                    if (cacheTimeOut != null) {
                        try {
                            timeout = Integer.parseInt(cacheTimeOut);
                        } catch (Exception var15) {
                            log.error("配置的超时时间解析错误, 使用默认时间，path: {}", path);
                        }
                    }

                    HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(path);
                    HystrixCommandKey commandKey = com.netflix.hystrix.HystrixCommandKey.Factory.asKey(path);
                    HystrixObservableCommand.Setter setter = HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationSemaphoreMaxConcurrentRequests(YhTimeOutGatewayFilterFactory.this.defaultSemaphore).withFallbackIsolationSemaphoreMaxConcurrentRequests(YhTimeOutGatewayFilterFactory.this.defaultSemaphore).withExecutionTimeoutInMilliseconds(timeout).withExecutionIsolationThreadTimeoutInMilliseconds(timeout));
                    HystrixPropertiesFactory.getCommandProperties(com.netflix.hystrix.HystrixCommandKey.Factory.asKey(path), HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout));
                    YhRouteHystrixCommand command = CustomTimeOutGatewayFilterFactory.this.new YhRouteHystrixCommand(setter, config.fallbackUri, exchange, chain, context, timeout, CustomTimeOutGatewayFilterFactory.this.defaultSemaphore);
                    int finalTimeout = timeout;
                    return Mono.create((s) -> {
                        Subscription sub = command.toObservable().subscribe(s::success, s::error, s::success);
                        s.onCancel(sub::unsubscribe);
                    }).onErrorResume((throwable) -> {
                        if (throwable instanceof HystrixRuntimeException) {
                            HystrixRuntimeException e = (HystrixRuntimeException)throwable;
                            HystrixRuntimeException.FailureType failureType = e.getFailureType();
                            switch (failureType) {
                                case TIMEOUT:
                                    String message = "请求达到熔断时间(ms): " + finalTimeout;
                                    return ResponseUtils.setReturnException(HttpStatus.REQUEST_TIMEOUT, exchange.getResponse(), PublicResult.build(HttpStatus.REQUEST_TIMEOUT.value(), message, (Object)null));
                                case SHORTCIRCUIT:
                                    return Mono.error(new ServiceUnavailableException());
                                case COMMAND_EXCEPTION:
                                    return Mono.error(e.getCause());
                            }
                        }

                        return Mono.error(throwable);
                    }).then();
                });
            }

            public String toString() {
                return GatewayToStringStyler.filterToStringCreator(CustomTimeOutGatewayFilterFactory.this).append("name", config.getName()).append("fallback", config.fallbackUri).toString();
            }
        };
    }

    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Data
    public static class Config {
        private String id;
        private String name;
        private HystrixObservableCommand.Setter setter;
        private URI fallbackUri;

        public void setFallbackUri(URI fallbackUri) {
            if (fallbackUri != null && !"forward".equals(fallbackUri.getScheme())) {
                throw new IllegalArgumentException("Hystrix Filter currently only supports 'forward' URIs, found " + fallbackUri);
            } else {
                this.fallbackUri = fallbackUri;
            }
        }
    }

    private class YhRouteHystrixCommand extends HystrixObservableCommand<Void> {
        private final URI fallbackUri;
        private final ServerWebExchange exchange;
        private final GatewayFilterChain chain;
        private final Context context;

        public YhRouteHystrixCommand(HystrixObservableCommand.Setter setter, URI fallbackUri, ServerWebExchange exchange, GatewayFilterChain chain, Context context, int timeout, int semaphore) {
            super(setter);
            this.fallbackUri = fallbackUri;
            this.exchange = exchange;
            this.chain = chain;
            this.context = context;
        }

        protected Observable<Void> construct() {
            return RxReactiveStreams.toObservable(this.chain.filter(this.exchange).subscriberContext(this.context));
        }

        protected Observable<Void> resumeWithFallback() {
            if (null == this.fallbackUri) {
                return super.resumeWithFallback();
            } else {
                URI uri = this.exchange.getRequest().getURI();
                boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
                URI requestUrl = UriComponentsBuilder.fromUri(uri).host((String)null).port((String)null).uri(this.fallbackUri).build(encoded).toUri();
                this.exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
                this.addExceptionDetails();
                ServerHttpRequest request = this.exchange.getRequest().mutate().uri(requestUrl).build();
                ServerWebExchange mutated = this.exchange.mutate().request(request).build();
                DispatcherHandler dispatcherHandler = (DispatcherHandler)YhTimeOutGatewayFilterFactory.this.dispatcherHandler.getIfAvailable();
                return RxReactiveStreams.toObservable(dispatcherHandler.handle(mutated));
            }
        }

        private void addExceptionDetails() {
            Throwable executionException = this.getExecutionException();
            Optional.ofNullable(executionException).ifPresent((exception) -> {
                this.exchange.getAttributes().put(ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR, exception);
            });
        }
    }
}
