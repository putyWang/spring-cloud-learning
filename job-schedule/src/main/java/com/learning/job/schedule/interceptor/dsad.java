package com.learning.job.schedule.interceptor;

import com.alibaba.fastjson.JSON;
import com.learning.core.utils.StringUtil;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.core.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {
    public PermissionInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        } else {
            boolean needLogin = true;
            boolean needAdminuser = false;
            HandlerMethod method = (HandlerMethod)handler;
            PermissionLimit permission = (PermissionLimit)method.getMethodAnnotation(PermissionLimit.class);
            if (permission != null) {
                needLogin = permission.limit();
                needAdminuser = permission.adminUser();
            }

            if (needLogin) {
                String token = request.getHeader("Authorization");
                if (StringUtil.isNotBlank(token)) {
                    boolean verify = TokenUtil.verify(token);
                    if (!verify) {
                        this.responseErrMsg(response);
                    }

                    return verify;
                } else {
                    this.responseErrMsg(response);
                    return false;
                }
            } else {
                return super.preHandle(request, response, handler);
            }
        }
    }

    private void responseErrMsg(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setStatus(401);
        PrintWriter out = httpServletResponse.getWriter();
        out.write(JSON.toJSONString(new ReturnT(ResultStatus.EXCEPTION_TOKEN.getValue(), ResultStatus.EXCEPTION_TOKEN.getReasonPhrase())));
        out.flush();
        out.close();
    }
}
