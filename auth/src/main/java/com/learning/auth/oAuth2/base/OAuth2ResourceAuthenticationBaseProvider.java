package com.learning.auth.oAuth2.base;

import cn.hutool.extra.spring.SpringUtil;
import com.learning.auth.utils.OAuth2ErrorCodesExpand;
import com.learning.auth.utils.ScopeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.*;

/**
 * @author putyWang
 * 处理自定义授权 AccessToken
 */
@Log4j2
public abstract class OAuth2ResourceAuthenticationBaseProvider <T extends OAuth2ResourceAuthenticationBaseToken>
        implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    private final AuthenticationManager authenticationManager;

    private final MessageSourceAccessor messages;

    public OAuth2ResourceAuthenticationBaseProvider(AuthenticationManager authenticationManager,
                                                    OAuth2AuthorizationService authorizationService,
                                                    OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;

        // 国际化配置
        this.messages = new MessageSourceAccessor(SpringUtil.getBean("securityMessageSource"), Locale.CHINA);
    }

    /**
     * 当前的请求客户端是否支持此模式
     * @param registeredClient
     */
    public abstract void checkClient(RegisteredClient registeredClient);

    /**
     * 根据参数生成 UsernamePasswordAuthenticationToken
     * @param reqParameters
     * @return
     */
    public abstract UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters);

    /**
     * 授权逻辑
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 1. 校验客户端principal是否已有效认证
        T resourceOwnerBaseAuthentication = (T) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(resourceOwnerBaseAuthentication);
        // 2.检验认证范围是否 supports AuthorizationGrantType.PASSWORD = password
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        checkClient(registeredClient);
        // 3.校验请求的scopes是否全部包含在授权客户端信息中
        Set<String> authorizedScopes = new LinkedHashSet<>();
        Set<String> scopes = resourceOwnerBaseAuthentication.getScopes();
        if (! CollectionUtils.isEmpty(scopes)) {
            if (!registeredClient.getScopes().containsAll(scopes)) {
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
            }
            authorizedScopes = new LinkedHashSet<>(scopes);
        }
        // 4.根据附加信息从manager中认证校验用户信息
        Map<String, Object> reqParameters = resourceOwnerBaseAuthentication.getAdditionalParameters();
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = buildToken(reqParameters);
            log.debug("got usernamePasswordAuthenticationToken={}", usernamePasswordAuthenticationToken);
            Authentication usernamePasswordAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            // 5. 构建Access token
            // -- 搭建 OAuth2TokenContext.build
            DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                    .registeredClient(registeredClient)
                    .principal(usernamePasswordAuthentication)
                    .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                    .authorizedScopes(authorizedScopes)
                    .authorizationGrantType(resourceOwnerBaseAuthentication.getAuthorizationGrantType())
                    .authorizationGrant(resourceOwnerBaseAuthentication);
            // -- 搭建 OAuth2TokenContext
            OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
            // -- 生成 token，判断是否为空
            OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
            if (generatedAccessToken == null) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the access token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }
            // -- 构建 OAuth2AccessToken
            OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                    generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                    generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
            // -- 构建 OAuth2Authorization.build
            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                    .withRegisteredClient(registeredClient)
                    .principalName(usernamePasswordAuthentication.getName())
                    .authorizationGrantType(resourceOwnerBaseAuthentication.getAuthorizationGrantType())
                    // 0.4.0 新增的方法
                    .authorizedScopes(authorizedScopes);
            // -- 赋值到 OAuth2Authorization.Builder
            if (generatedAccessToken instanceof ClaimAccessor) {
                authorizationBuilder.id(accessToken.getTokenValue())
                        .token(accessToken,
                                (metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                                        ((ClaimAccessor) generatedAccessToken).getClaims()))
                        // 0.4.0 新增的方法
                        .authorizedScopes(authorizedScopes)
                        .attribute(Principal.class.getName(), usernamePasswordAuthentication);
            } else {
                authorizationBuilder.id(accessToken.getTokenValue()).accessToken(accessToken);
            }
            // 6.构建 Refresh token ----
            // -- 判断是否需要生成 Refresh token
            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                    // Do not issue refresh token to public client
                    ! clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
                // -- 修改 OAuth2TokenContext.build 的值，并搭建 OAuth2TokenContext
                tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
                // -- 生成 token，判断是否为空
                OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
                if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate the refresh token.", ERROR_URI);
                    throw new OAuth2AuthenticationException(error);
                }
                //6.赋值到 OAuth2Authorization.Builder
                refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
                authorizationBuilder.refreshToken(refreshToken);
            }
            // 7.搭建 OAuth2Authorization，并存储客户端已认证信息，进行持久化
            OAuth2Authorization authorization = authorizationBuilder.build();
            this.authorizationService.save(authorization);
            // 8.返回 OAuth2AccessTokenAuthenticationToken
            log.debug("returning OAuth2AccessTokenAuthenticationToken");
            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken,
                    refreshToken, Objects.requireNonNull(authorization.getAccessToken().getClaims()));
        }
        catch (Exception ex) {
            log.error("problem in authenticate", ex);
            throw oAuth2AuthenticationException(authentication, (AuthenticationException) ex);
        }
    }

    /**
     * 根据认证信息获取 token
     * @param authentication
     * @return
     */
    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {

        OAuth2ClientAuthenticationToken clientPrincipal = null;

        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }

        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * 登录异常转换为oauth2异常
     * @param authentication 身份验证
     * @param authenticationException 身份验证异常
     * @return {@link OAuth2AuthenticationException}
     */
    private OAuth2AuthenticationException oAuth2AuthenticationException(Authentication authentication,
                                                                        AuthenticationException authenticationException) {
        if (authenticationException instanceof UsernameNotFoundException) {
            return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USERNAME_NOT_FOUND,
                    this.messages.getMessage("JdbcDaoImpl.notFound", new Object[] { authentication.getName() },
                            "Username {0} not found"),
                    ""));
        }
        if (authenticationException instanceof BadCredentialsException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(OAuth2ErrorCodesExpand.BAD_CREDENTIALS, this.messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), ""));
        }
        if (authenticationException instanceof LockedException) {
            return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USER_LOCKED, this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"), ""));
        }
        if (authenticationException instanceof DisabledException) {
            return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USER_DISABLE,
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"),
                    ""));
        }
        if (authenticationException instanceof AccountExpiredException) {
            return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.USER_EXPIRED, this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"), ""));
        }
        if (authenticationException instanceof CredentialsExpiredException) {
            return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodesExpand.CREDENTIALS_EXPIRED,
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                            "User credentials have expired"),
                    ""));
        }
        if (authenticationException instanceof ScopeException) {
            return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE,
                    this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "invalid_scope"), ""));
        }
        return new OAuth2AuthenticationException(OAuth2ErrorCodesExpand.UN_KNOW_LOGIN_ERROR);
    }
}
