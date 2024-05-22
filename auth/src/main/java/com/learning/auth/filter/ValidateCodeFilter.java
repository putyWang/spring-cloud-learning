package com.learning.auth.filter;

import cn.hutool.core.util.StrUtil;
import com.learning.auth.config.properties.AuthSecurityConfigProperties;
import com.learning.core.constants.SecurityConstants;
import com.learning.core.constants.CacheConstants;
import com.learning.core.exception.ValidateCodeException;
import com.learning.core.utils.SpringContextHolder;
import com.learning.core.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author putyWang
 * 登录前置处理器： 前端密码传输密文解密，验证码处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateCodeFilter extends OncePerRequestFilter {

    private final AuthSecurityConfigProperties authSecurityConfigProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1.只有登陆 url 才进行验证
        if (!SecurityConstants.OAUTH_TOKEN_URL.equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }
        // 2.刷新token的请求，执行后续过滤器
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 3.客户端配置跳过验证码
        boolean isIgnoreClient = authSecurityConfigProperties.getIgnoreClients().contains(WebUtils.getClientId());
        if (isIgnoreClient) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4.校验验证码
        try {
            checkCode();
            filterChain.doFilter(request, response);
        } catch (ValidateCodeException validateCodeException) {
            throw new OAuth2AuthenticationException(validateCodeException.getMessage());
        }
    }

    /**
     * 校验验证码
     */
    private void checkCode() throws ValidateCodeException {
        // 1.验证码不能为空
        Optional<HttpServletRequest> request = WebUtils.getRequest();
        String code = request.get().getParameter("code");
        if (StrUtil.isBlank(code)) {
            throw new ValidateCodeException("验证码不能为空");
        }
        // 2.拼接 redis 中的 key
        String randomStr = request.get().getParameter("randomStr");
        String mobile = request.get().getParameter("mobile");
        if (StrUtil.isNotBlank(mobile)) {
            randomStr = mobile;
        }
        String key = CacheConstants.DEFAULT_CODE_KEY + randomStr;
        RedisTemplate<String, String> redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        // 3.从 redis 中获取验证码
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw new ValidateCodeException("验证码不合法");
        }
        Object codeObj = redisTemplate.opsForValue().get(key);
        if (codeObj == null) {
            throw new ValidateCodeException("验证码不合法");
        }
        // 4.判断验证码与当前是否相同
        String saveCode = codeObj.toString();
        if (StrUtil.isBlank(saveCode)) {
            redisTemplate.delete(key);
            throw new ValidateCodeException("验证码不合法");
        }

        if (! StrUtil.equals(saveCode, code)) {
            redisTemplate.delete(key);
            throw new ValidateCodeException("验证码不合法");
        }
        redisTemplate.delete(key);
    }
}
