package com.learning.job.executor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.learning.job.handler.IJobHandler;
import com.learning.job.log.XxlJobFileAppender;
import com.learning.job.thread.JobLogFileCleanThread;
import com.learning.job.thread.JobThread;
import com.learning.job.thread.TriggerCallbackThread;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j2
public class XxlJobExecutor {
    private String logPath;
    private int logRetentionDays;
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap();
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap();

    public XxlJobExecutor() {
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    public void start() {
        XxlJobFileAppender.initLogPath(this.logPath);
        JobLogFileCleanThread.getInstance().start((long)this.logRetentionDays);
        TriggerCallbackThread.getInstance().start();
    }

    public void destroy() {
        if (jobThreadRepository.size() > 0) {
            Iterator var1 = jobThreadRepository.entrySet().iterator();

            while(var1.hasNext()) {
                Map.Entry<Integer, JobThread> item = (Map.Entry)var1.next();
                removeJobThread(item.getKey(), "web container destroy and kill the job.");
            }

            jobThreadRepository.clear();
        }

        jobHandlerRepository.clear();
        JobLogFileCleanThread.getInstance().toStop();
        TriggerCallbackThread.getInstance().toStop();
    }

    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        log.info(">>>>>>>>>>> yh-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static IJobHandler loadJobHandler(String name) {
        return (IJobHandler)jobHandlerRepository.get(name);
    }

    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        log.info(">>>>>>>>>>> yh-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});
        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static void removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

    }

    public static JobThread loadJobThread(int jobId) {
        return jobThreadRepository.get(jobId);
    }
}
