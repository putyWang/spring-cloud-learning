package com.learning.security.config;

import cn.hutool.core.util.ArrayUtil;
import com.learning.security.config.properties.PermitAllUrlProperties;
import com.learning.security.manager.CustomAccessDecisionManager;
import com.learning.security.metadata.CustomFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@RequiredArgsConstructor
@EnableConfigurationProperties(PermitAllUrlProperties.class)
@Configuration
public class ResourceServerConfig {

    /**
     * 对外暴露的接口列表
     */
    private final PermitAllUrlProperties permitAllUrl;

    protected final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

    private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain securityFilterChain(HttpSecurity http, AccessDecisionManager accessDecisionManager, FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) throws Exception {
        // 1.配置端点白名单方放行
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers(ArrayUtil.toArray(permitAllUrl.getUrls(), String.class))
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                                                     @Override
                                                     public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                                                         //设置自定义访问决策管理器
                                                         o.setAccessDecisionManager(accessDecisionManager);
                                                         //设置自定义的权限数据源
                                                         o.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                                                         return o;
                                                     }
                                                 }))
                .oauth2ResourceServer(
                        oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector)) //配置资源服务信息
                                .authenticationEntryPoint(resourceAuthExceptionEntryPoint)
                                .bearerTokenResolver(new BearerTokenExtractor(permitAllUrl))) //配置accesstoken提取器
                .headers()
                .frameOptions()
                .disable()
                .and()
                // 忽略掉相关端点的 csrf
                .csrf()
                .disable();

        return http.build();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager(){
        return new CustomAccessDecisionManager();
    }

    @Bean
    public FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource(PermissionConfig permissionConfig, PermitAllUrlProperties permitAllUrlProperties){
        return new CustomFilterInvocationSecurityMetadataSource(permissionConfig, permitAllUrlProperties);
    }
}
