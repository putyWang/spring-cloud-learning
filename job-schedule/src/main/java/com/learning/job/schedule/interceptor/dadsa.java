package com.learning.job.schedule.interceptor;

import com.learning.job.schedule.core.utils.FtlUtil;
import com.learning.job.schedule.core.utils.I18nUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class CookieInterceptor extends HandlerInterceptorAdapter {
    public CookieInterceptor() {
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.getCookies() != null && request.getCookies().length > 0) {
            HashMap<String, Cookie> cookieMap = new HashMap();
            Cookie[] var6 = request.getCookies();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Cookie ck = var6[var8];
                cookieMap.put(ck.getName(), ck);
            }

            modelAndView.addObject("cookieMap", cookieMap);
        }

        if (modelAndView != null) {
            modelAndView.addObject("I18nUtil", FtlUtil.generateStaticModel(I18nUtil.class.getName()));
        }

        super.postHandle(request, response, handler, modelAndView);
    }
}
