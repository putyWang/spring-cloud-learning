package com.learning.interrogation.server.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/5 上午12:52
 */
@Configuration
public class DruidConfig {

    // 注册 StatViewServlet（监控页面）
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(
                new StatViewServlet(), "/druid/*"  // 访问路径，与配置文件中的 url-pattern 一致
        );
        // 配置参数（等价于配置文件）
        bean.addInitParameter("allow", "192.168.3.60");        // 允许访问的 IP
        bean.addInitParameter("loginUsername", "admin");     // 用户名
        bean.addInitParameter("loginPassword", "123456");     // 密码
        bean.addInitParameter("resetEnable", "false");        // 禁用重置功能
        return bean;
    }

    // 注册 WebStatFilter（监控 SQL 执行）
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>(
                new WebStatFilter()
        );
        bean.addUrlPatterns("/*");
        // 排除不需要监控的路径（如静态资源、登录接口等）
        bean.addInitParameter("exclusions", "/static/*,/login*,/logout*");
        return bean;
    }
}
