package com.learning.auth.config;

import com.learning.auth.oAuth2.CustomOAuth2AccessTokenGenerator;
import com.learning.auth.oAuth2.core.CustomOAuth2TokenCustomizer;
import com.learning.auth.oAuth2.core.DaoAuthenticationProvider;
import com.learning.core.constants.SecurityConstants;
import com.learning.auth.filter.PasswordDecoderFilter;
import com.learning.auth.filter.ValidateCodeFilter;
import com.learning.auth.handler.AuthenticationFailureEventHandler;
import com.learning.auth.handler.AuthenticationSuccessEventHandler;
import com.learning.auth.oAuth2.core.FormIdentityLoginConfigurer;
import com.learning.auth.oAuth2.password.OAuth2ResourceAuthenticationPasswordConverter;
import com.learning.auth.oAuth2.password.OAuth2ResourceAuthenticationPasswordProvider;
import com.learning.auth.oAuth2.sms.OAuth2ResourceAuthenticationSmsConverter;
import com.learning.auth.oAuth2.sms.OAuth2ResourceAuthenticationSmsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

/**
 * 配置授权过滤器
 * @author putyWang
 * @date 2021/9/23
 */
@RequiredArgsConstructor
@Configuration
public class AuthorizationServerConfig {

    private final OAuth2AuthorizationService authorizationService;

    private final PasswordDecoderFilter passwordDecoderFilter;

    private final ValidateCodeFilter validateCodeFilter;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authenticationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        //1. 使用 HttpSecurity 获取 OAuth 2.1 配置中的 OAuth2AuthorizationServerConfigurer 对象
        // -- 缺省配置：authorizeRequests.anyRequest().authenticated()、
        // -- csrf.ignoringRequestMatchers(endpointsMatcher) 等等
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = http
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        //2. 配置个性化客户端认证
        // -- 增加验证码过滤器
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class);
        // -- 增加密码解密过滤器
        http.addFilterBefore(passwordDecoderFilter, UsernamePasswordAuthenticationFilter.class);
        // -- 个性化认证授权端点
        authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> { // 个性化认证授权端点
            tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter()) // 注入自定义的授权认证Converter
                    .accessTokenResponseHandler(new AuthenticationSuccessEventHandler()) // 登录成功处理器
                    .errorResponseHandler(new AuthenticationFailureEventHandler()); // 登录失败处理器
        }).clientAuthentication(oAuth2ClientAuthenticationConfigurer -> // 个性化客户端认证
                        oAuth2ClientAuthenticationConfigurer.errorResponseHandler(new AuthenticationFailureEventHandler())) // 处理客户端认证异常
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint // 授权码端点个性化confirm页面
                        .consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI));

        DefaultSecurityFilterChain securityFilterChain = authorizationServerConfigurer
                .authorizationService(authorizationService)// redis存储token的实现
                .authorizationServerSettings(
                        AuthorizationServerSettings.builder().issuer(SecurityConstants.PROJECT_LICENSE).build())
                // 授权码登录的登录页个性化
                .and()
                .apply(new FormIdentityLoginConfigurer())
                .and()
                .build();
        // 3.注入自定义授权模式认证实现类
        addCustomOAuth2GrantAuthenticationProvider(http);
        //返回
        return securityFilterChain;
    }

    /**
     * request -> xToken 注入自定义请求转换器
     * @return DelegatingAuthenticationConverter
     */
    @Bean
    public AuthenticationConverter accessTokenRequestConverter(){
        //new一个token转换器委托器，其中包含自定义密码模式认证转换器和刷新令牌认证转换器
        return new DelegatingAuthenticationConverter(Arrays.asList(
                new OAuth2ResourceAuthenticationPasswordConverter(),
                new OAuth2ResourceAuthenticationSmsConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),
                new OAuth2ClientCredentialsAuthenticationConverter(),
                new OAuth2AuthorizationCodeAuthenticationConverter(),
                new OAuth2AuthorizationCodeRequestAuthenticationConverter()));
    }

    @Bean
    public OAuth2TokenGenerator oAuth2TokenGenerator(){
        CustomOAuth2AccessTokenGenerator accessTokenGenerator = new CustomOAuth2AccessTokenGenerator();
        // 注入Token 增加关联用户信息
        accessTokenGenerator.setAccessTokenCustomizer(new CustomOAuth2TokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
    }

    /**
     * 注入授权模式实现提供方
     * <p>
     * 1. 密码模式 </br>
     * 2. 短信登录 </br>
     */
    @SuppressWarnings("unchecked")
    private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http) {
        // 1.从shareObject中获取到授权管理业务类（主要负责管理已认证的授权信息）
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        // 2.从shareObject中获取到认证管理类
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        // 3.提供自定义用户认证提供方
        http.authenticationProvider(new DaoAuthenticationProvider());
        // 4.将自定义处理密码模式的授权提供方添加到安全配置中
        http.authenticationProvider(new OAuth2ResourceAuthenticationPasswordProvider(authenticationManager, authorizationService, oAuth2TokenGenerator()));
        // 5.将手机验证码提供方添加到安全配置中
        http.authenticationProvider(new OAuth2ResourceAuthenticationSmsProvider(authenticationManager, authorizationService, oAuth2TokenGenerator()));
    }
}
