package com.learning.orm.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.learning.orm.injector.SqlInjector;
import lombok.AllArgsConstructor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MpBeanPostProcessor
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Component
@AllArgsConstructor
public class MpBeanPostProcessor implements BeanPostProcessor {

    private SqlInjector sqlInjector;

    private SqlSessionFactory sqlSessionFactory;

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if ("sqlSessionTemplate".equals(beanName)) {
            Configuration configuration = this.sqlSessionFactory.getConfiguration();
            GlobalConfigUtils.getGlobalConfig(configuration)
                    .setSqlInjector(this.sqlInjector);
        }

        return bean;
    }
}
