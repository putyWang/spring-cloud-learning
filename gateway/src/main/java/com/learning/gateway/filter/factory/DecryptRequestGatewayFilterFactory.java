package com.learning.gateway.filter.factory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class DecryptRequestGatewayFilterFactory extends ConfigurableModifyRequestBodyGatewayFilterFactory<byte[], byte[]> implements Ordered {
    public DecryptRequestGatewayFilterFactory(DecryptRequestRewriteFunction rewriteFunction) {
        super(rewriteFunction);
    }

    public GatewayFilter apply(ModifyRequestBodyGatewayFilterFactory.Config config) {
        return super.apply(config);
    }

    public int getOrder() {
        return Integer.MIN_VALUE + 3;
    }
}