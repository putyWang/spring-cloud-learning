package com.learning.gateway.config;

//import com.learning.exception.CustomErrorWebExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * 路由异常处理拦截配置
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureBefore(WebFluxAutoConfiguration.class)
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
public class ExceptionAutoConfiguration {

    private ServerProperties serverProperties;

    private ApplicationContext applicationContext;

    private ResourceProperties resourceProperties;

    private List<ViewResolver> viewResolvers;

    private ServerCodecConfigurer serverCodecConfigurer;

    public ExceptionAutoConfiguration(ServerProperties serverProperties,
                                      ResourceProperties resourceProperties,
                                      ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                      ServerCodecConfigurer serverCodecConfigurer,
                                      ApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider
                .getIfAvailable(() -> Collections.emptyList());
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

//    /**
//     * 注入自定义异常拦截器
//     * @param errorAttributes
//     * @return
//     */
//    @Bean
//    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
//        DefaultErrorWebExceptionHandler exceptionHandler = new CustomErrorWebExceptionHandler(
//                errorAttributes, this.resourceProperties,
//                this.serverProperties.getError(), this.applicationContext);
//        exceptionHandler.setViewResolvers(this.viewResolvers);
//        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
//        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
//        return exceptionHandler;
//    }
}
