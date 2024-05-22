package com.learning.auth.oAuth2.password;

import com.learning.auth.oAuth2.base.OAuth2ResourceAuthenticationBaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * @author putyWang
 * 继承 OAuth2ResourceAuthenticationBaseToken 生成 token
 */
public class OAuth2ResourceAuthenticationPasswordToken extends OAuth2ResourceAuthenticationBaseToken {
    public OAuth2ResourceAuthenticationPasswordToken(AuthorizationGrantType authorizationGrantType,
                                                     Authentication clientPrincipal, Set<String> scopes,
                                                     Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }
}
