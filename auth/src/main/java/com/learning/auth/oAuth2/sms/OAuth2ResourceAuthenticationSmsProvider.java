package com.learning.auth.oAuth2.sms;

import com.learning.auth.oAuth2.base.OAuth2ResourceAuthenticationBaseProvider;
import com.learning.core.domain.constants.SecurityConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Map;

/**
 * @author putyWang
 * 拿到最终AccessToken授权信息的
 */
@Log4j2
public class OAuth2ResourceAuthenticationSmsProvider
        extends OAuth2ResourceAuthenticationBaseProvider<OAuth2ResourceAuthenticationSmsToken> {

    /**
     * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
     * provided parameters.
     * @param authenticationManager
     * @param authorizationService the authorization service
     * @param tokenGenerator the token generator
     * @since 0.2.3
     */
    public OAuth2ResourceAuthenticationSmsProvider(AuthenticationManager authenticationManager,
                                                        OAuth2AuthorizationService authorizationService,
                                                        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        super(authenticationManager, authorizationService, tokenGenerator);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        boolean supports = OAuth2ResourceAuthenticationSmsToken.class.isAssignableFrom(authentication);
        log.debug("supports authentication={} returning {}", authentication, supports);
        return supports;
    }

    @Override
    public void checkClient(RegisteredClient registeredClient) {
        assert registeredClient != null;
        if (!registeredClient.getAuthorizationGrantTypes()
                .contains(new AuthorizationGrantType(SecurityConstants.MOBILE))) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters) {
        String phone = (String) reqParameters.get(SecurityConstants.SMS_PARAMETER_NAME);
        return new UsernamePasswordAuthenticationToken(phone, null);
    }
}
