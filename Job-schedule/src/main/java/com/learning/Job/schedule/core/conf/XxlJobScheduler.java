package com.learning.Job.schedule.core.conf;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@DependsOn({"xxlJobAdminConfig"})
@Log4j2
public class XxlJobScheduler implements InitializingBean, DisposableBean {

    @Autowired
    private AdminBiz adminBiz;
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<>();

    public XxlJobScheduler() {
    }

    public void afterPropertiesSet() throws Exception {
        this.initI18n();
        JobRegistryMonitorHelper.getInstance().start();
        JobFailMonitorHelper.getInstance().start();
        JobScheduleHelper.getInstance().start();
        logger.info(">>>>>>>>> init yh-job admin success.");
    }

    public void destroy() throws Exception {
        JobScheduleHelper.getInstance().toStop();
        JobTriggerPoolHelper.toStop();
        JobRegistryMonitorHelper.getInstance().toStop();
        JobFailMonitorHelper.getInstance().toStop();
    }

    private void initI18n() {
        ExecutorBlockStrategyEnum[] var1 = ExecutorBlockStrategyEnum.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ExecutorBlockStrategyEnum item = var1[var3];
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }

    }

    public static void invokeAdminService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        if (address != null && address.trim().length() != 0) {
            address = address.trim();
            ExecutorBiz executorBiz = (ExecutorBiz)executorBizRepository.get(address);
            if (executorBiz != null) {
                return executorBiz;
            } else {
                ExecutorBiz executorBiz = new ExecutorBizImpl();
                executorBizRepository.put(address, executorBiz);
                return executorBiz;
            }
        } else {
            return null;
        }
    }
}
