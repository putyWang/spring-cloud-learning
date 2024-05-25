package com.learning.job.biz.impl;

import java.util.Date;

import com.learning.job.biz.ExecutorBiz;
import com.learning.job.biz.model.LogResult;
import com.learning.job.biz.model.TriggerParam;
import com.learning.job.enums.ExecutorBlockStrategyEnum;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.executor.XxlJobExecutor;
import com.learning.job.glue.GlueFactory;
import com.learning.job.glue.GlueTypeEnum;
import com.learning.job.handler.IJobHandler;
import com.learning.job.handler.impl.GlueJobHandler;
import com.learning.job.handler.impl.ScriptJobHandler;
import com.learning.job.log.XxlJobFileAppender;
import com.learning.job.thread.JobThread;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j2
public class ExecutorBizImpl implements ExecutorBiz {

    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    public ReturnT<String> idleBeat(int jobId) {
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobId);
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        return isRunningOrHasQueue ? new ReturnT(500, "job thread is running or has trigger queue.") : ReturnT.SUCCESS;
    }

    public ReturnT<String> kill(int jobId) {
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobId);
        if (jobThread != null) {
            XxlJobExecutor.removeJobThread(jobId, "scheduling center kill job.");
            return ReturnT.SUCCESS;
        } else {
            return new ReturnT(200, "job thread aleady killed.");
        }
    }

    public ReturnT<LogResult> log(long logDateTim, long logId, int fromLineNum) {
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logDateTim), logId);
        LogResult logResult = XxlJobFileAppender.readLog(logFileName, fromLineNum);
        return new ReturnT(logResult);
    }

    public ReturnT<String> run(TriggerParam triggerParam) {
        JobThread jobThread = XxlJobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        IJobHandler originJobHandler;
        if (GlueTypeEnum.BEAN == glueTypeEnum) {
            originJobHandler = XxlJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
            if (jobThread != null && jobHandler != originJobHandler) {
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";
                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                jobHandler = originJobHandler;
                if (jobHandler == null) {
                    return new ReturnT(500, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }
        } else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {
            if (jobThread != null && (!(jobThread.getHandler() instanceof GlueJobHandler) || ((GlueJobHandler)jobThread.getHandler()).getGlueUpdatetime() != triggerParam.getGlueUpdateTime())) {
                removeOldReason = "change job source or glue type, and terminate the old job thread.";
                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                try {
                    originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdateTime());
                } catch (Exception var7) {
                    Exception e = var7;
                    log.error(e.getMessage(), e);
                    return new ReturnT(500, e.getMessage());
                }
            }
        } else {
            if (glueTypeEnum == null || !glueTypeEnum.isScript()) {
                return new ReturnT(500, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
            }

            if (jobThread != null && (!(jobThread.getHandler() instanceof ScriptJobHandler) || ((ScriptJobHandler)jobThread.getHandler()).getGlueUpdatetime() != triggerParam.getGlueUpdateTime())) {
                removeOldReason = "change job source or glue type, and terminate the old job thread.";
                jobThread = null;
                jobHandler = null;
            }

            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdateTime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        }

        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), (ExecutorBlockStrategyEnum)null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT(500, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();
                    jobThread = null;
                }
            } else if (ExecutorBlockStrategyEnum.DISCARD_CURRENT == blockStrategy && jobThread.isRunningOrHasQueue()) {
                log.warn("任务正在执行，丢弃新的任务");
                return new ReturnT(200, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_CURRENT.getTitle());
            }
        }

        if (jobThread == null) {
            jobThread = XxlJobExecutor.registJobThread(triggerParam.getJobId(), (IJobHandler)jobHandler, removeOldReason);
        }

        ReturnT<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }
}