package com.learning.security.config;

import cn.hutool.extra.spring.SpringUtil;
import com.learning.security.model.LearningUser;
import com.learning.security.service.CustomUserDetailService;
import com.learning.core.domain.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 使用 token 获取有效用户信息
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService authorizationService;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization oldAuthorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (Objects.isNull(oldAuthorization)) {
            throw new InvalidBearerTokenException(token);
        }
        // 1.客户端模式默认返回
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(oldAuthorization.getAuthorizationGrantType())) {
            return new DefaultOAuth2AuthenticatedPrincipal(oldAuthorization.getPrincipalName(),
                    Objects.requireNonNull(oldAuthorization.getAccessToken().getClaims()),
                    AuthorityUtils.NO_AUTHORITIES);
        }
        // 2.构造当前客户端支持的用户信息获取服务
        Map<String, CustomUserDetailService> userDetailsServiceMap = SpringUtil
                .getBeansOfType(CustomUserDetailService.class);

        Optional<CustomUserDetailService> optional = userDetailsServiceMap.values()
                .stream()
                .filter(service -> service.support(Objects.requireNonNull(oldAuthorization).getRegisteredClientId(),
                        oldAuthorization.getAuthorizationGrantType().getValue()))
                .max(Comparator.comparingInt(Ordered::getOrder));

        // 3.根据授权信息获取对应的详细用户信息
        UserDetails userDetails = null;
        try {
            Object principal = Objects.requireNonNull(oldAuthorization).getAttributes().get(Principal.class.getName());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
            Object tokenPrincipal = usernamePasswordAuthenticationToken.getPrincipal();
            userDetails = optional.get().loadUserByUser((LearningUser) tokenPrincipal);
        }
        catch (UsernameNotFoundException notFoundException) {
            log.warn("用户不不存在 {}", notFoundException.getLocalizedMessage());
            throw notFoundException;
        }
        catch (Exception ex) {
            log.error("资源服务器 introspect Token error {}", ex.getLocalizedMessage());
        }

        // 4.注入扩展属性,方便上下文获取客户端ID
        LearningUser user = (LearningUser) userDetails;
        Objects.requireNonNull(user)
                .getAttributes()
                .put(SecurityConstants.CLIENT_ID, oldAuthorization.getRegisteredClientId());
        return user;
    }
}
