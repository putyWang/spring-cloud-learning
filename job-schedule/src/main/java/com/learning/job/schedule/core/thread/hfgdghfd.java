package com.learning.job.schedule.core.thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.learning.job.schedule.core.trigger.TriggerTypeEnum;
import com.learning.job.schedule.core.trigger.XxlJobTrigger;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JobTriggerPoolHelper {

    private ThreadPoolExecutor fastTriggerPool;
    private ThreadPoolExecutor slowTriggerPool;
    private volatile long minTim;
    private volatile ConcurrentMap<Integer, AtomicInteger> jobTimeoutCountMap;
    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    public JobTriggerPoolHelper() {
        this.fastTriggerPool = new ThreadPoolExecutor(50, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(1000), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "yh-job, admin JobTriggerPoolHelper-fastTriggerPool-" + r.hashCode());
            }
        });
        this.slowTriggerPool = new ThreadPoolExecutor(10, 100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(2000), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "yh-job, admin JobTriggerPoolHelper-slowTriggerPool-" + r.hashCode());
            }
        });
        this.minTim = System.currentTimeMillis() / 60000L;
        this.jobTimeoutCountMap = new ConcurrentHashMap();
    }

    public void addTrigger(final int jobId, final TriggerTypeEnum triggerType, final int failRetryCount, final String executorShardingParam, final String executorParam, final String addressList) {
        ThreadPoolExecutor triggerPool_ = this.fastTriggerPool;
        AtomicInteger jobTimeoutCount = this.jobTimeoutCountMap.get(jobId);
        if (jobTimeoutCount != null && jobTimeoutCount.get() > 10) {
            triggerPool_ = this.slowTriggerPool;
        }

        triggerPool_.execute(new Runnable() {
            public void run() {
                long start = System.currentTimeMillis();
                boolean var16 = false;

                long minTim_now;
                long costx;
                AtomicInteger timeoutCountx;
                label114: {
                    try {
                        var16 = true;
                        XxlJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
                        var16 = false;
                        break label114;
                    } catch (Exception var17) {
                        Exception e = var17;
                        e.printStackTrace();
                        JobTriggerPoolHelper.log.error(e.getMessage(), e);
                        var16 = false;
                    } finally {
                        if (var16) {
                            long minTim_nowx = System.currentTimeMillis() / 60000L;
                            if (JobTriggerPoolHelper.this.minTim != minTim_nowx) {
                                JobTriggerPoolHelper.this.minTim = minTim_nowx;
                                JobTriggerPoolHelper.this.jobTimeoutCountMap.clear();
                            }

                            long cost = System.currentTimeMillis() - start;
                            if (cost > 500L) {
                                AtomicInteger timeoutCount = JobTriggerPoolHelper.this.jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                                if (timeoutCount != null) {
                                    timeoutCount.incrementAndGet();
                                }
                            }

                        }
                    }

                    minTim_now = System.currentTimeMillis() / 60000L;
                    if (JobTriggerPoolHelper.this.minTim != minTim_now) {
                        JobTriggerPoolHelper.this.minTim = minTim_now;
                        JobTriggerPoolHelper.this.jobTimeoutCountMap.clear();
                    }

                    costx = System.currentTimeMillis() - start;
                    if (costx > 500L) {
                        timeoutCountx = (AtomicInteger)JobTriggerPoolHelper.this.jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                        if (timeoutCountx != null) {
                            timeoutCountx.incrementAndGet();
                            return;
                        }
                    }

                    return;
                }

                minTim_now = System.currentTimeMillis() / 60000L;
                if (JobTriggerPoolHelper.this.minTim != minTim_now) {
                    JobTriggerPoolHelper.this.minTim = minTim_now;
                    JobTriggerPoolHelper.this.jobTimeoutCountMap.clear();
                }

                costx = System.currentTimeMillis() - start;
                if (costx > 500L) {
                    timeoutCountx = (AtomicInteger)JobTriggerPoolHelper.this.jobTimeoutCountMap.putIfAbsent(jobId, new AtomicInteger(1));
                    if (timeoutCountx != null) {
                        timeoutCountx.incrementAndGet();
                    }
                }

            }
        });
    }

    public void stop() {
        this.fastTriggerPool.shutdownNow();
        this.slowTriggerPool.shutdownNow();
        log.info(">>>>>>>>> yh-job trigger thread pool shutdown success.");
    }

    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        helper.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
    }

    public static void toStop() {
        helper.stop();
    }
}

