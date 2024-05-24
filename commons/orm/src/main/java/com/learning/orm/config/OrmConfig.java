package com.learning.orm.config;

import com.baomidou.mybatisplus.core.MybatisPlusVersion;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.learning.orm.config.properties.OrmProperties;
import com.learning.orm.injector.SqlInjector;
import com.learning.orm.interceptor.TableNameInterceptor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName: dasda
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Configuration
@MapperScan({"com.learning.orm.mapper"})
@EnableConfigurationProperties({OrmProperties.class})
@Log4j2
public class OrmConfig {

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public TableNameInterceptor tablenameInterceptor() {
        log.info("*************************************");
        log.info("**  _ _   |_  _ _|_. ___ _ |    _  **");
        log.info("** | | |\\/|_)(_| | |_\\  |_)||_|_\\  **");
        log.info("**      /               |          **");
        log.info("**       orm v{}    {}    **", OrmAutoConfigure.class.getPackage().getImplementationVersion(), MybatisPlusVersion.getVersion());
        log.info("*************************************");
        return new TableNameInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlInjector yhSqlInjector() {
        return new SqlInjector();
    }

    @Bean({"restTemplateOrm"})
    @LoadBalanced
    public RestTemplate restTemplateOrm() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        simpleClientHttpRequestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        OrmUtilConfig.restTemplateOrm = restTemplate;
        return restTemplate;
    }
}
