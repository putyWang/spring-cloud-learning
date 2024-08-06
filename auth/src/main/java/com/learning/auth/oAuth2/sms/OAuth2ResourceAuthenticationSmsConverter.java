package com.learning.auth.oAuth2.sms;

import com.learning.auth.oAuth2.base.OAuth2ResourceAuthenticationBaseConverter;
import com.learning.auth.utils.OAuth2EndpointUtils;
import com.learning.core.domain.constants.SecurityConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
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
public class OAuth2ResourceAuthenticationSmsConverter
        extends OAuth2ResourceAuthenticationBaseConverter<OAuth2ResourceAuthenticationSmsToken> {
    /**
     * 是否支持此convert
     * @param grantType 授权类型
     * @return
     */
    @Override
    public boolean support(String grantType) {
        return SecurityConstants.MOBILE.equals(grantType);
    }

    @Override
    public OAuth2ResourceAuthenticationSmsToken buildToken(Authentication clientPrincipal, Set requestedScopes,
                                                                Map additionalParameters) {
        return new OAuth2ResourceAuthenticationSmsToken(new AuthorizationGrantType(SecurityConstants.MOBILE),
                clientPrincipal, requestedScopes, additionalParameters);
    }

    /**
     * 校验扩展参数 密码模式密码必须不为空
     * @param request 参数列表
     */
    @Override
    public void checkParams(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
        // 电话号码不能为空
        String phone = parameters.getFirst(SecurityConstants.SMS_PARAMETER_NAME);
        if (!StringUtils.hasText(phone) || parameters.get(SecurityConstants.SMS_PARAMETER_NAME).size() != 1) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.SMS_PARAMETER_NAME,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
    }
}
