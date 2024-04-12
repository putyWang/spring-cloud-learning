package com.learning.web.config.mybatis;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.learning.web.config.mybatis.injector.CustomSqlInjector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 持久层mybatis-plus自动装配
 *
 * @author felix
 */
@Configuration
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
public class MybatisPlusConfiguration {
    /**
     * 乐观锁，需要在version字段上加@Version
     *
     * @return
     */
    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 填充sql
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(value = CustomSqlInjector.class)
    public CustomSqlInjector customSqlInjector() {
        return new CustomSqlInjector();
    }

    /**
     * 分页
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(value = PaginationInnerInterceptor.class)
    public PaginationInnerInterceptor paginationInterceptor() {
        return new PaginationInnerInterceptor();
    }

    /**
     * 默认填充
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(value = {MetaObjectHandler.class})
    @ConditionalOnProperty(value = {"mybatis-plus.meta-object-handler.enable"}, matchIfMissing = true)
    public MybatisPlusMetaObjectHandler mybatisPlusMetaObjectHandler() {
        return new MybatisPlusMetaObjectHandler();
    }


    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public DataScopeInterceptor dataScopeInterceptor(DataSource dataSource, CacheHelper cacheHelper) {
//        return new DataScopeInterceptor(dataSource, cacheHelper);
//    }
}