package com.learning.auth.oAuth2.base;

import com.learning.auth.utils.OAuth2EndpointUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author putyWang
 * 将request的params、body入参转化一下，
 * 并和principal一起拿到token，
 * 这里只需要检验入参格式等信息
 */
public abstract class OAuth2ResourceAuthenticationBaseConverter <T extends OAuth2ResourceAuthenticationBaseToken>
        implements AuthenticationConverter {

    /**
     * 是否支持此convert
     * @param grantType 授权类型
     * @return
     */
    public abstract boolean support(String grantType);

    /**
     * 自定义校验参数
     * @param request 请求
     */
    public void checkParams(HttpServletRequest request) {
        // 默认不校验自定义参数
    }

    /**
     * 构建具体类型的token
     * @param clientPrincipal
     * @param requestedScopes
     * @param additionalParameters
     * @return
     */
    public abstract T buildToken(Authentication clientPrincipal, Set<String> requestedScopes, Map<String, Object> additionalParameters);

    /**
     * 构建具体类型的token
     * @param request
     * @return
     */
    @Override
    public Authentication convert(HttpServletRequest request) {
        // 1. 校验是否支持此convert,根据grantType校验
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!support(grantType)) {
            return null;
        }
        // 2.校验参数，个性化的看具体需求
        checkParams(request);
        // 3.获取当前已经认证的客户端信息，这里的数据，来自于 OAuth2ClientAuthenticationFilter 过滤器创建的
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ErrorCodes.INVALID_CLIENT,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        // 4.获取请求的scopes信息
        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
        // -- 存在作用域时，作用域只能有一个
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        // -- 获取作用域列表
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }
        // 5.获取附加信息
        Map<String, Object> additionalParameters = parameters.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE)
                        && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
        // 6.创建token
        return buildToken(clientPrincipal, requestedScopes, additionalParameters);
    }
}
