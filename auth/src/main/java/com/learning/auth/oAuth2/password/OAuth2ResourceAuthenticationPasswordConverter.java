package com.learning.auth.oAuth2.password;

import com.learning.auth.oAuth2.base.OAuth2ResourceAuthenticationBaseConverter;
import com.learning.auth.utils.OAuth2EndpointUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * @author putyWang
 * 将request的params、body入参转化一下，
 * 并和principal一起拿到token，
 * 这里只需要检验入参格式等信息
 */
public class OAuth2ResourceAuthenticationPasswordConverter
        extends OAuth2ResourceAuthenticationBaseConverter<OAuth2ResourceAuthenticationPasswordToken> {
    /**
     * 支持密码模式
     * @param grantType 授权类型
     */
    @Override
    public boolean support(String grantType) {
        return AuthorizationGrantType.PASSWORD.getValue().equals(grantType);
    }

    @Override
    public OAuth2ResourceAuthenticationPasswordToken buildToken(Authentication clientPrincipal,
                                                                Set requestedScopes,
                                                                Map additionalParameters) {
        return new OAuth2ResourceAuthenticationPasswordToken(AuthorizationGrantType.PASSWORD, clientPrincipal,
                requestedScopes, additionalParameters);
    }

    /**
     * 校验扩展参数 密码模式密码必须不为空
     * @param request 参数列表
     */
    @Override
    public void checkParams(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
        // 1.用户名不能为空
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(username) || parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.USERNAME,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 密码不能为空
        String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
        if (!StringUtils.hasText(password) || parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.PASSWORD,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
    }
}
