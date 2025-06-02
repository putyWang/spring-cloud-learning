package com.learning.interrogation.server.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @ClassName: UserContext
 * @Description: TODO
 * @Author: wr
 * @Date: 2019/12/05 18:06
 * @Version V1.0
 **/
public class TokenContext implements Serializable {

    //token表示码
    private static String LOGINED_KEY = "CTOKEN";

    /**
     * 获取当前线程绑定的token
     *
     * @return
     */
    public static String getToken() {
        String token = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            token = (String) requestAttributes.getAttribute(LOGINED_KEY, RequestAttributes.SCOPE_REQUEST);
            if(StrUtil.isEmpty(token)){
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                do {
                    token = request.getHeader(LOGINED_KEY);
                    if (StrUtil.isNotBlank(token)) {
                        break;
                    }
                    token = request.getParameter(LOGINED_KEY);
                    if (StrUtil.isNotBlank(token)) {
                        break;
                    }
                    break;
                } while (true);

            }
        }
        if (StrUtil.isEmpty(token)) {
            throw new RuntimeException("CTOKEN不存在");
        }
        return token;
    }

    /**
     * 将token绑定到当前线程
     *
     * @param Token
     */
    public static void setToken(String Token) {
        RequestContextHolder.getRequestAttributes().setAttribute(LOGINED_KEY, Token, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 将用户登录对象从当前线程销毁
     */
    public static void removeToken() {
        RequestContextHolder.getRequestAttributes().removeAttribute(LOGINED_KEY, RequestAttributes.SCOPE_REQUEST);
    }

}
