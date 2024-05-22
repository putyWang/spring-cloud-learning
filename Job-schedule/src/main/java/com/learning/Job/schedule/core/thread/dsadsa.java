package com.learning.Job.schedule.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.enums.RegistryConfig.RegistType;
import com.xxl.job.core.util.HttpUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Log4j2
public class JobRegistryMonitorHelper {
    private static JobRegistryMonitorHelper instance = new JobRegistryMonitorHelper();
    private static ExecutorService executorService;
    private Thread registryThread;
    private volatile boolean toStop = false;

    public JobRegistryMonitorHelper() {
    }

    public static JobRegistryMonitorHelper getInstance() {
        return instance;
    }

    public void start() {
        this.registryThread = new Thread(new Runnable() {
            public void run() {
                while(!JobRegistryMonitorHelper.this.toStop) {
                    try {
                        List<XxlJobGroup> groupList = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().findByAddressType(0);
                        groupList.forEach((xxlJobGroup) -> {
                            XxlJobAdminConfig.getAdminConfig().xxlJobService().registryByDiscovery(xxlJobGroup, RegistType.EXECUTOR.name());
                        });
                        List ids;
                        if (!CollectionUtils.isEmpty(groupList)) {
                            ids = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().findDead(90, new Date());
                            if (!CollectionUtils.isEmpty(ids)) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().removeDead(ids);
                            }
                        }

                        ids = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().findByAddressType((Integer)null);
                        ids.forEach((xxlJobGroup) -> {
                            if (!StringUtils.isEmpty(xxlJobGroup.getAddressList())) {
                                String[] appList = xxlJobGroup.getAddressList().split(",");
                                String[] var2 = appList;
                                int var3 = appList.length;

                                for(int var4 = 0; var4 < var3; ++var4) {
                                    String app = var2[var4];
                                    JobRegistryMonitorHelper.executorService.submit(() -> {
                                        String url = "http://" + app + "/v1/executor/list";

                                        try {
                                            Map<String, String> map = new HashMap(1);
                                            map.put("adminName", XxlJobAdminConfig.getAdminConfig().getName());
                                            HttpUtil.postFromParam(url, map);
                                        } catch (Exception var3) {
                                            Exception e = var3;
                                            e.printStackTrace();
                                            JobRegistryMonitorHelper.logger.error("notice app error : {}, url address : {}", e.toString(), url);
                                        }

                                    });
                                }
                            }

                        });
                    } catch (Exception var4) {
                        Exception ex = var4;
                        if (!JobRegistryMonitorHelper.this.toStop) {
                            JobRegistryMonitorHelper.logger.error(">>>>>>>>>>> yh-job, job registry monitor thread error:{}", ex);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(30L);
                    } catch (InterruptedException var3) {
                        InterruptedException e = var3;
                        if (!JobRegistryMonitorHelper.this.toStop) {
                            JobRegistryMonitorHelper.logger.error(">>>>>>>>>>> yh-job, job registry monitor thread error:{}", e);
                        }
                    }
                }

                JobRegistryMonitorHelper.logger.info(">>>>>>>>>>> yh-job, job registry monitor thread stop");
            }
        });
        this.registryThread.setDaemon(true);
        this.registryThread.setName("yh-job, admin JobRegistryMonitorHelper");
        this.registryThread.start();
    }

    public void toStop() {
        this.toStop = true;
        this.registryThread.interrupt();

        try {
            this.registryThread.join();
        } catch (InterruptedException var2) {
            InterruptedException e = var2;
            logger.error(e.getMessage(), e);
        }

    }

    static {
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 10L, TimeUnit.SECONDS, new LinkedBlockingDeque(200), (r) -> {
            return new Thread(r, "notice_thread_pool");
        });
    }
}
