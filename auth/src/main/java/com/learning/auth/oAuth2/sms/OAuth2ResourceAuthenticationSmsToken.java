package com.learning.auth.oAuth2.sms;

import com.learning.auth.oAuth2.base.OAuth2ResourceAuthenticationBaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * @author putyWang
 * 继承 AbstractAuthenticationToken 生成 token
 */
public class OAuth2ResourceAuthenticationSmsToken extends OAuth2ResourceAuthenticationBaseToken {
    public OAuth2ResourceAuthenticationSmsToken(AuthorizationGrantType authorizationGrantType,
                                                     Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }
}
