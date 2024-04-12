package com.learning.core.config.shiro;

import com.learning.core.filter.CustomAuthenticationFilter;
import com.learning.core.handler.DefaultAuthorizationAttributeSourceAdvisor;
import com.learning.core.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置类
 */
@Log4j2
@Configuration
public class ShiroConfig {

    @Resource
    private ShiroProperties shiroProperties;

    /**
     * 自定义realm
     *
     * @return
     */
    @Bean
    public CommonRealm getRealm() {
        return generateRealm();
    }

    protected CommonRealm generateRealm() {
        return new CommonRealm();
    }

    /**
     * 安全管理器
     *
     * @param realm realm域
     * @return SecurityManager
     */
    @Bean
    public SecurityManager securityManager(CommonRealm realm) {
        //默认安全管理器
        //将自定义的realm交给安全管理器管理
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        //自定义session管理器
        securityManager.setSessionManager(sessionManager());
        //自定义缓存实现
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    /**
     * shiro过滤器工厂
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        //shiro过滤器工厂
        ShiroFilterFactoryBean filterFactory = new ShiroFilterFactoryBean();
        //设置安全管理器
        filterFactory.setSecurityManager(securityManager);
        LinkedHashMap<String, Filter> filterMap = new LinkedHashMap<>();
        //自定义认证过滤器
        filterMap.put("auth", new CustomAuthenticationFilter());
        filterFactory.setFilters(filterMap);
        //设置过滤链
        Map<String, String> filterChainMap = new LinkedHashMap<>();
        //anon  游客即可访问
        String authIgnore = shiroProperties.getAuthIgnore();
        log.info("不需要鉴权路径：{}", authIgnore);
        if (!StringUtils.isEmpty(authIgnore)) {
            Arrays.asList(authIgnore.split(",")).forEach((anonUrl) -> {
                filterChainMap.put(anonUrl, "anon");
            });
        }
        //authc 需经过验证才能访问  auth自定义的过滤策略
        filterChainMap.put("/**", "auth");
        filterFactory.setFilterChainDefinitionMap(filterChainMap);

        return filterFactory;
    }

    /**
     * shiro启用注解授权，并注入自定义的注解授权处理类
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new DefaultAuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        /**
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole等shiro注解，会导致该方法无法映射请求，导致返回404。
         * 加入这项配置能解决这个bug
         */
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }


    /**
     * 配置redis缓存管理器,用户、角色、权限实体类需序列化
     *
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        //设置redis管理器
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }


    /**
     * session管理器
     *
     * @return
     */
    public DefaultWebSessionManager sessionManager() {
        CommonWebSessionManager sessionManager = new CommonWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        //设置session超时时间(单位毫秒),设置为-1000L永不过期
        ShiroProperties.SessionProperty session = shiroProperties.getSession();
        sessionManager.setGlobalSessionTimeout(session.getTimeout());
        //删除过期的session
        sessionManager.setDeleteInvalidSessions(true);
        //定时检查session
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //可自定义sessionId
        sessionManager.setSessionIdCookie(new SimpleCookie(session.getSessionName()));
        return sessionManager;
    }

    /**
     * redisSessiondao，实现redis的增删改查，交给shiro管理，shiro使用的是jedis
     * 也可自定义
     *
     * @return
     */
    private RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * redis管理器
     *
     * @return
     */
    private RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        //设置redis ip 端口 密码
        ShiroProperties.RedisProperty redis = shiroProperties.getRedis();
        redisManager.setHost(redis.getHost());
        redisManager.setPort(redis.getPort());
        redisManager.setPassword(redis.getPassword());
        redisManager.setTimeout(redis.getTimeout());
        redisManager.setExpire(redis.getExpire());
        return redisManager;
    }
}
