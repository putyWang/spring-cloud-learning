package com.learning.auth.filter;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.learning.auth.config.properties.AuthSecurityConfigProperties;
import com.learning.core.constants.SecurityConstants;
import com.learning.core.servlet.RepeatBodyRequestWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author putyWang
 * 登录前置处理器： 密码解密过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordDecoderFilter extends OncePerRequestFilter {

    private final AuthSecurityConfigProperties authSecurityConfigProperties;

    private static final String PASSWORD = "password";

    private static final String KEY_ALGORITHM = "AES";

    static {
        // 关闭hutool 强制关闭Bouncy Castle库的依赖
        SecureUtil.disableBouncyCastle();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 1.不是登录请求，直接向下执行
        if (!StrUtil.containsAnyIgnoreCase(request.getRequestURI(), SecurityConstants.OAUTH_TOKEN_URL)) {
            chain.doFilter(request, response);
            return;
        }

        // 2.将请求流转换为可多次读取的请求流
        RepeatBodyRequestWrapper requestWrapper = new RepeatBodyRequestWrapper(request);
        Map<String, String[]> parameterMap = requestWrapper.getParameterMap();

        // 3.构建前端对应解密AES 因子
        AES aes = new AES(Mode.CFB, Padding.NoPadding,
                new SecretKeySpec(authSecurityConfigProperties.getEncodeKey().getBytes(), KEY_ALGORITHM),
                new IvParameterSpec(authSecurityConfigProperties.getEncodeKey().getBytes()));

        parameterMap.forEach((k, v) -> {
            String[] values = parameterMap.get(k);
            if (!PASSWORD.equals(k) || ArrayUtil.isEmpty(values)) {
                return;
            }

            // 解密密码
            String decryptPassword = aes.decryptStr(values[0]);
            parameterMap.put(k, new String[] { decryptPassword });
        });
        chain.doFilter(requestWrapper, response);
    }
}
