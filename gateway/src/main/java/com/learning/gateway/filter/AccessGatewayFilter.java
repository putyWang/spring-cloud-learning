package com.learning.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.learning.core.model.UserContext;
import com.learning.core.utils.StringUtils;
import com.learning.core.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;

/**
 * 请求url权限校验
 */
@Component
@Slf4j
public class AccessGatewayFilter implements GlobalFilter, Ordered {

    private static final String X_CLIENT_TOKEN_USER = "x-client-token-user";
    private static final String TOKEN = "token";

    // 直接在springbean属性上添加，
    @CreateCache(area = "default", name = "user.permission.", expire = -1, cacheType = CacheType.REMOTE)
    private Cache<String, Object> cache;

    @Value("${gate.login.url:/login}")
    private String loginUrl = "/login";

    @Value("${gate.ignore.authentication.startWith:/oauth}")
    private String ignoreUrls = "/oauth";

    /**
     * token 加密盐
     */
    @Value("${gate.token.salt:token}")
    private String salt = "token";

    /**
     *
     */

    /**
     * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
     * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求相关参数
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(TOKEN);
        String method = request.getMethodValue();
        String url = request.getPath().value();
        log.debug("url:{},method:{},headers:{}", url, method, request.getHeaders());
        // 2.不需要网关签权的url直接跳过
        if (ignoreAuthentication(url)) {
            return chain.filter(exchange);
        }

        // 3. token为空时 或是否有权限，若有权限进入下一个filter
        if (! StringUtils.isEmpty(token) && hasPermission(token, url, method)) {
            ServerHttpRequest.Builder builder = request.mutate();
            // 4.将解析后的uuid传输给服务用户获取用户信息
            builder.header(X_CLIENT_TOKEN_USER, TokenUtil.decodeToken(token, salt));
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }
        return unauthorized(exchange);
    }

    /**
     * 未登陆，返回401
     *
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = serverWebExchange.getResponse()
                .bufferFactory().wrap(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }


    /**
     * 判断url是否不需要鉴权
     * @param url
     * @return
     */
    private boolean ignoreAuthentication(String url) {
        return url.equals(loginUrl) ? true : Arrays.stream(ignoreUrls.split(",")).anyMatch(ignoreUrl -> url.trim().matches(StringUtils.trim(ignoreUrl)));
    }

    /**
     * 验证是否具有权限
     */
    private boolean hasPermission (String token, String url, String method) {
        // 1.获取作为redis key的uuid
        String uuid = TokenUtil.decodeToken(token, salt);
        // 2.获取redis中token对应的用户信息
        Object uerInfo = cache.get(uuid);

        if(uerInfo != null && uerInfo instanceof String) {
            // 3.获取对应用户拥有权限
            UserContext userContext = JSONObject.parseObject((String) uerInfo, UserContext.class);
            Set<String> authorities = userContext.getAuthorities();

            // 4.获取url，method对应的权限信息
            String permission = cache.get(method + ":" + url).toString();

            return authorities.stream().anyMatch(authority -> authority.equals(permission));
        }

        return false;
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
