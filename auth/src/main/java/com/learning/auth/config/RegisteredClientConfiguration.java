package com.learning.auth.config;

import com.learning.auth.service.feignService.ClientDetailsService;
import com.learning.auth.service.RedisOAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * 客户端信息的持久化管理、认证信息的持久化管理
 * @author putyWang
 * @date 2021/9/23
 */
@RequiredArgsConstructor
@Configuration
public class RegisteredClientConfiguration {

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate){
        //使用 redis 授权管理业务
        return new RedisOAuth2AuthorizationService(redisTemplate);
    }

    /**
     * 注册一个客户端应用
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(ClientDetailsService clientDetailsService) {
        // 远程调用持久化
        return new RemoteRegisteredClientRepository(clientDetailsService);
    }
}
