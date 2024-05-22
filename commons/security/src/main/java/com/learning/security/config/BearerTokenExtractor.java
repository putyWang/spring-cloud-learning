package com.learning.security.config;

import com.learning.security.config.properties.PermitAllUrlProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token 提取逻辑
 */
public class BearerTokenExtractor implements BearerTokenResolver {

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-:._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    private boolean allowFormEncodedBodyParameter = false;

    /**
     * 支持 请求明文传参
     */
    private boolean allowUriQueryParameter = true;

    private String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final PermitAllUrlProperties urlProperties;

    public BearerTokenExtractor(PermitAllUrlProperties urlProperties) {
        this.urlProperties = urlProperties;
    }

    /**
     * 提取请求 token
     * @param request
     * @return
     */
    @Override
    public String resolve(HttpServletRequest request) {
        // 1.不鉴权路径直接跳过
        boolean match = urlProperties.getUrls()
                .stream()
                .anyMatch(url -> pathMatcher.match(url, request.getRequestURI()));

        if (match) {
            return null;
        }
        // 2.获取当请求中的 token
        final String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
        final String parameterToken = isParameterTokenSupportedForRequest(request) ? resolveFromRequestParameters(request) : null;
        // 3.token 无法包含双 token
        if (authorizationHeaderToken != null) {
            if (parameterToken != null) {
                final BearerTokenError error = BearerTokenErrors
                        .invalidRequest("请求头与 query 参数中都包含 token 参数");
                throw new OAuth2AuthenticationException(error);
            }
            return authorizationHeaderToken;
        }
        if (parameterToken != null && isParameterTokenEnabledForRequest(request)) {
            return parameterToken;
        }
        return null;
    }

    /**
     * 解析请求头中包含的 token
     * @param request
     * @return
     */
    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        // 1.请求头中的 token 必须以 bearer 开头
        String authorization = request.getHeader(this.bearerTokenHeaderName);
        if (! StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return null;
        }
        // 2.token 必须符合格式
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (! matcher.matches()) {
            BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
            throw new OAuth2AuthenticationException(error);
        }
        return matcher.group("token");
    }

    /**
     * 解析请求参数中包含的 token
     * @param request
     * @return
     */
    private static String resolveFromRequestParameters(HttpServletRequest request) {
        String[] values = request.getParameterValues("access_token");
        if (values == null || values.length == 0) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }
        BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
        throw new OAuth2AuthenticationException(error);
    }

    private boolean isParameterTokenSupportedForRequest(final HttpServletRequest request) {
        return (("POST".equals(request.getMethod())
                && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
                || "GET".equals(request.getMethod()));
    }

    private boolean isParameterTokenEnabledForRequest(final HttpServletRequest request) {
        return ((this.allowFormEncodedBodyParameter && "POST".equals(request.getMethod())
                && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
                || (this.allowUriQueryParameter && "GET".equals(request.getMethod())));
    }
}
