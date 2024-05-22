package com.learning.gateway.filter.factory;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

@Log4j2
public class ConfigurableModifyRequestBodyGatewayFilterFactory<T, R> extends ModifyRequestBodyGatewayFilterFactory {

    protected final ModifyRequestBodyGatewayFilterFactory.Config config;

    public ConfigurableModifyRequestBodyGatewayFilterFactory(RewriteFunction<T, R> rewriteFunction) {
        Class<T> tClass = null;
        Class<R> rClass = null;
        Type[] genericInterfaces = rewriteFunction.getClass().getGenericInterfaces();

        for(Type genericInterface : genericInterfaces ) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
                if (parameterizedType.getRawType().equals(RewriteFunction.class)) {
                    Type[] actualTypes = parameterizedType.getActualTypeArguments();
                    if (actualTypes.length == 2) {
                        tClass = (Class)actualTypes[0];
                        rClass = (Class)actualTypes[1];
                    }
                }
            }
        }

        Objects.requireNonNull(tClass, "RewriteFunction's actualType Class<T> can not be recognized");
        Objects.requireNonNull(rClass, "RewriteFunction's actualType Class<R> can not be recognized");
        if (log.isDebugEnabled()) {
            log.debug("ConfigurableModifyRequestBodyGatewayFilterFactory: {}", this.getClass().getName());
            log.debug("inClass {}", tClass.getName());
            log.debug("outClass {}", rClass.getName());
            log.debug("rewriteFunction {}", rewriteFunction.getClass().getName());
        }

        this.config = this.newConfig().setRewriteFunction(tClass, rClass, rewriteFunction);
    }

    public GatewayFilter apply(ModifyRequestBodyGatewayFilterFactory.Config c) {
        return super.apply(this.config);
    }
}