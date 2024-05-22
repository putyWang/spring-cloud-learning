package com.learning.gateway.filter.factory;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Log4j2
public class FixedRouteGatewayFilterFactory extends AbstractGatewayFilterFactory<FixedRouteGatewayFilterFactory.Config> {

    private static final String FIXED_ROUTER = "FIXED_ROUTER";

    @Value("${devMode:false}")
    boolean devMode;

    @Autowired
    DiscoveryClient discoveryClient;

    public FixedRouteGatewayFilterFactory() {
        super(Config.class);
    }

    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (this.devMode) {
                String rawPath = request.getURI().getRawPath();
                HttpHeaders headers = request.getHeaders();
                HttpMethod httpMethod = request.getMethod();
                MultiValueMap<String, String> queryParams = request.getQueryParams();
                if (headers.containsKey("FIXED_ROUTER")) {
                    String router = (String)headers.get("FIXED_ROUTER").get(0);
                    if (!StringUtils.isEmpty(router)) {
                        if (router.startsWith("lb://")) {
                            String serviceId = router.replace("lb://", "");
                            List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);
                            if (instances.isEmpty()) {
                                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "服务不在线");
                            }

                            Collections.shuffle(instances);
                            ServiceInstance serviceInstance = (ServiceInstance)instances.get(0);
                            router = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/";
                        }

                        URI uri = UriComponentsBuilder.fromHttpUrl(router + rawPath).queryParams(queryParams).build().toUri();
                        ServerHttpRequest serverHttpRequest = request.mutate().uri(uri).method(httpMethod).headers((httpHeaders) -> {
                        }).build();
                        Route route = (Route)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                        Route newRoute = ((Route.AsyncBuilder)((Route.AsyncBuilder)((Route.AsyncBuilder)((Route.AsyncBuilder)Route.async().asyncPredicate(route.getPredicate()).filters(route.getFilters())).id(route.getId())).order(route.getOrder())).uri(uri)).build();
                        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, newRoute);
                        return chain.filter(exchange.mutate().request(serverHttpRequest).build());
                    }
                }
            }

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    @Data
    public static class Config {
    }
}