package com.learning.auth.oAuth2.base;

import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author putyWang
 * 继承 AbstractAuthenticationToken 生成 token
 */
public abstract class OAuth2ResourceAuthenticationBaseToken extends AbstractAuthenticationToken {

    @Getter
    private final AuthorizationGrantType authorizationGrantType;

    @Getter
    private final Authentication clientPrincipal;

    @Getter
    private final Set<String> scopes;

    @Getter
    private final Map<String, Object> additionalParameters;

    /**
     * token 构造
     * @param authorizationGrantType
     * @param clientPrincipal
     * @param scopes
     * @param additionalParameters
     */
    public OAuth2ResourceAuthenticationBaseToken(AuthorizationGrantType authorizationGrantType,
                                                 Authentication clientPrincipal, @Nullable Set<String> scopes,
                                                 @Nullable Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.notNull(authorizationGrantType, "authorizationGrantType cannot be null");
        Assert.notNull(clientPrincipal, "clientPrincipal cannot be null");
        this.authorizationGrantType = authorizationGrantType;
        this.clientPrincipal = clientPrincipal;
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections.unmodifiableMap(
                additionalParameters != null ? new HashMap<>(additionalParameters) : Collections.emptyMap());
    }

    /**
     * 扩展模式一般不需要密码
     * @return
     */
    @Override
    public Object getCredentials() {
        return "";
    }

    /**
     * 获取用户名
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.clientPrincipal;
    }
}
