package com.learning.auth.handler;

import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.core.cache.RedisCache;
import com.learning.core.holder.UserContext;
import com.learning.core.utils.StringUtils;
import com.learning.core.utils.TokenUtil;
import com.learning.auth.entity.LearningUser;
import com.learning.auth.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Base64;
import java.util.UUID;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientDetailsService clientDetailsService;
    @CreateCache(area = "default", name = "user.permission.", expire = -1, cacheType = CacheType.REMOTE)
    private Cache<String, Object> cache;
    /**
     * 是否允许重复登陆
     */
    @Value("${auth.login.repeat.enable:false}")
    private boolean loginRepeatEnable = false;
    /**
     * token 加密盐
     */
    @Value("${gate.token.salt:token}")
    private String salt = "token";
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. 从请求头中获取 ClientId
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }

        String[] tokens = this.extractAndDecodeHeader(header, request);
        String clientId = tokens[0];
        String clientSecret = tokens[1];

        // 2. 通过 ClientDetailsService 获取 ClientDetails
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        // 3. 校验 ClientId和 ClientSecret的正确性
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId:" + clientId + "对应的信息不存在");
        } else if (! passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret不正确");
        }

        // 4. 获取登陆用户信息
        LearningUser user = (LearningUser)authentication.getPrincipal();
        UserContext userInfo = new UserContext();
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAuthorities(AuthorityUtils.authorityListToSet(user.getAuthorities()));
        userInfo.setMenus(user.getMenus());
        userInfo.setRoles(user.getRoles());

        LoginVo loginVo = new LoginVo();
        loginVo.setUserInfo(userInfo);

        // 5. 验证用户是否已登陆
        String token = (String)cache.get(user.getUsername());

        // 6.用户已登陆 是否允许多用户登陆
        if (! StringUtils.isEmpty(token)) {
            // 7.更新保存的用户信息
            String uuid = TokenUtil.decodeToken(token, salt);
            cache.remove(uuid);
            cache.put(uuid, JSON.toJSONString(userInfo));

            if (loginRepeatEnable) {
                // 8. 允许情况下
                loginVo.setToken(token);
            }else {
                // 9.不允许则抛出异常
                throw new UserPrincipalNotFoundException("该用户已在其他地方登陆");
            }
        }else {
            String uuid = UUID.randomUUID().toString();
            cache.put(uuid, JSON.toJSONString(userInfo));
            token = TokenUtil.encodeToken(uuid, salt);
            cache.put(user.getUsername(), token);
            // 10. 允许情况下
            loginVo.setToken(token);
        }

        // 11. 返回 登陆相关参数
        log.info("登录成功");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(loginVo));
    }

    private String[] extractAndDecodeHeader(String header, HttpServletRequest request) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
    }
}
