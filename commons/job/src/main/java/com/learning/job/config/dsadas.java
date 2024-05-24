package com.learning.job.config;

import com.learning.job.executor.impl.XxlJobSpringExecutor;
import com.learning.job.utils.DiscoveryUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@ComponentScan({"com.xxl.job.core.endpoint"})
@Log4j2
public class JobConfiguration implements ApplicationContextAware {
    private static String logPath;
    private static int logRetentionDays;
    public static String adminPath;

    public JobConfiguration() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        new DiscoveryUtil((LookupService)applicationContext.getBean(LookupService.class));
        Environment environment = applicationContext.getEnvironment();
        String logpath = environment.getProperty("yanhua.cloud.job.executor.logpath");
        String logretentiondays = environment.getProperty("yanhua.cloud.job.executor.logretentiondays");
        adminPath = environment.getProperty("yanhua.cloud.job.admin-Address");
        logPath = StringUtils.isEmpty(logpath) ? "/opt/yanhua/logs/yh-job/jobhandler" : logpath;
        logRetentionDays = StringUtils.isEmpty(logretentiondays) ? 7 : Integer.parseInt(logretentiondays);
    }

    @Bean(
            initMethod = "start",
            destroyMethod = "destroy"
    )
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> yh-job-{} config init.", JobConfiguration.class.getPackage().getImplementationVersion());
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }
}
