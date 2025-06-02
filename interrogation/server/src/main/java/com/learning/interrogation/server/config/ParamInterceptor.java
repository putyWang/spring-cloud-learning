package com.learning.interrogation.server.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.learning.interrogation.domain.annotion.Login;
import com.learning.interrogation.domain.annotion.NoLogin;
import com.learning.interrogation.server.util.MyUserTokenInfo;
import com.learning.interrogation.server.util.RedisUtil;
import com.learning.interrogation.server.util.TokenContext;
import com.learning.interrogation.domain.po.user.TokenInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ParamInterceptor
 * @Description: 请求拦截类
 * @Author: wr
 * @Date: 2019/12/05 16:53
 * @Version V2.1
 **/
@Slf4j
@Component
public class ParamInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 是否开启对CTOKEN的授权验证
     */
    @Value("${weiXinConfig.AUTHENTICATION_AUTHORITY: true}")
    private boolean AUTHENTICATION_AUTHORITY;

    /**
     * CTOKEN
     */
    private final static String TOKEN = "CTOKEN";

    /**
     * AUTH_CODE
     */
    private final static String AUTH_CODE = "authCode";

    /**
     * GET请求
     */
    private final static String GET_METHOD = "GET";

    /**
     * error
     */
    private final static String ERROR_STR = "error";

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,Object handler) throws Exception {
        // 1 是否开启 CSRF 安全校验，及CSRF白名单校验

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 2 有@NoLogin注解的不需登录验证
            if (ObjectUtil.isNotNull(handlerMethod.getMethodAnnotation(NoLogin.class))) {
                return true;
            }
            // 5 没有 @Login 注解的 get 请求需登录验证
            if (GET_METHOD.equals(httpServletRequest.getMethod()) &&
                    ObjectUtil.isNull(handlerMethod.getMethodAnnotation(Login.class))) {
                return true;
            }
        }
        // 6 cToken 验证
        cTokenValid(httpServletRequest);
        return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     * @param httpServletRequest 请求对象
     * @param httpServletResponse 响应对象
     * @param o controller 方法参数
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
        /*请求结束以后清除token*/

    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行
     * （主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) {
        // 1 将用户登录对象从当前线程销毁
        TokenContext.removeToken();
    }

    /**
     * cToken 验证
     * @param httpServletRequest 请求对象
     */
    private void cTokenValid(HttpServletRequest httpServletRequest) throws Exception {
        // 1 获取 cToken 值
        String cToken = httpServletRequest.getHeader(TOKEN);

        //绕过token请求地址验证
        String requestUri = httpServletRequest.getRequestURI();

        //若是请求出错，放过拦截，错误交由框架处理
        if (requestUri.contains(ERROR_STR)) {
            return;
        }
        //开启CTOEKN 微信授权验证
        if (AUTHENTICATION_AUTHORITY) {
            //CTOKEN为空
            if (StrUtil.isBlank(cToken)) {
                throw new RuntimeException("授权信息异常：未获取到授权信息！");
            }
        }
        TokenContext.setToken(cToken);

        TokenInfoPo userInfoForToken = MyUserTokenInfo.getUserInfoForToken();
        log.info("当前请求账户信息：{}", JSONUtil.toJsonStr(userInfoForToken));

        //获取登陆用户账户ID
        String userId = userInfoForToken.getUserId();
        String key = "user:login:user:id" + userId;
        Object obj = redisUtil.get(key);
        if (obj == null) {
            throw new RuntimeException("登录信息已过期！");
        } else {
            log.info("重置登录用户userId：{} 的有效时间！", userId);
            redisUtil.set(key, userId, 7L, TimeUnit.DAYS);
        }
    }
}
