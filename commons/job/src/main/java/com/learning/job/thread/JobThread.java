package com.learning.job.thread;

import com.learning.job.biz.model.HandleCallbackParam;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.biz.model.TriggerParam;
import com.learning.job.executor.XxlJobExecutor;
import com.learning.job.handler.IJobHandler;
import com.learning.job.log.XxlJobFileAppender;
import com.learning.job.log.XxlJobLogger;
import com.learning.job.utils.ShardingUtil;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Log4j2
public class JobThread extends Thread {
    private int jobId;
    private IJobHandler handler;
    private LinkedBlockingQueue<TriggerParam> triggerQueue;
    private Set<Long> triggerLogIdSet;
    private volatile boolean toStop = false;
    private String stopReason;
    private boolean running = false;
    private int idleTimes = 0;

    public JobThread(int jobId, IJobHandler handler) {
        this.jobId = jobId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingQueue();
        this.triggerLogIdSet = Collections.synchronizedSet(new HashSet());
    }

    public IJobHandler getHandler() {
        return this.handler;
    }

    public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
        if (this.triggerLogIdSet.contains(triggerParam.getLogId())) {
            log.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
            return new ReturnT(500, "repeate trigger job, logId:" + triggerParam.getLogId());
        } else {
            this.triggerLogIdSet.add(triggerParam.getLogId());
            this.triggerQueue.add(triggerParam);
            return ReturnT.SUCCESS;
        }
    }

    public void toStop(String stopReason) {
        this.toStop = true;
        this.stopReason = stopReason;
    }

    public boolean isRunningOrHasQueue() {
        return this.running || this.triggerQueue.size() > 0;
    }

    public void run() {
        Throwable e;
        try {
            this.handler.init();
        } catch (Throwable var26) {
            e = var26;
            log.error(e.getMessage(), e);
        }

        ReturnT executeResult;
        TriggerParam triggerParam;
        while(!this.toStop) {
            this.running = false;
            ++this.idleTimes;
            triggerParam = null;
            executeResult = null;
            boolean var16 = false;

            ReturnT stopResult;
            label340: {
                try {
                    var16 = true;
                    triggerParam = this.triggerQueue.poll(3L, TimeUnit.SECONDS);
                    if (triggerParam != null) {
                        this.running = true;
                        this.idleTimes = 0;
                        this.triggerLogIdSet.remove(triggerParam.getLogId());
                        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
                        XxlJobFileAppender.contextHolder.set(logFileName);
                        ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));
                        log.info("----------- 任务调度执行器开始 -----------{}", triggerParam.toString());
                        XxlJobLogger.log("<br>----------- yh-job job execute start -----------<br>----------- Param:" + triggerParam.getExecutorParams(), new Object[0]);
                        if (triggerParam.getExecutorTimeout() > 0) {
                            Thread futureThread = null;

                            try {
                                final TriggerParam triggerParamTmp = triggerParam;
                                FutureTask<ReturnT<String>> futureTask = new FutureTask((Callable<ReturnT<String>>) () ->
                                        JobThread.this.handler.execute(triggerParamTmp.getExecutorParams())
                                );
                                futureThread = new Thread(futureTask);
                                futureThread.start();
                                executeResult = (ReturnT)futureTask.get((long)triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
                            } catch (TimeoutException e1) {
                                XxlJobLogger.log("<br>----------- yh-job job execute timeout", new Object[0]);
                                XxlJobLogger.log(e1);
                                executeResult = new ReturnT(IJobHandler.FAIL_TIMEOUT.getCode(), "job execute timeout ");
                            } finally {
                                futureThread.interrupt();
                            }
                        } else {
                            executeResult = this.handler.execute(triggerParam.getExecutorParams());
                        }

                        if (executeResult == null) {
                            executeResult = IJobHandler.FAIL;
                        } else {
                            executeResult.setMsg(executeResult != null && executeResult.getMsg() != null && executeResult.getMsg().length() > 50000 ? executeResult.getMsg().substring(0, 50000).concat("...") : executeResult.getMsg());
                            executeResult.setContent((Object)null);
                        }

                        log.info("----------- 任务调度执行器结束 -----------" + executeResult.toString());
                        XxlJobLogger.log("<br>----------- yh-job job execute end(finish) -----------<br>----------- ReturnT:" + executeResult, new Object[0]);
                        var16 = false;
                    } else if (this.idleTimes > 30) {
                        XxlJobExecutor.removeJobThread(this.jobId, "excutor idel times over limit.");
                        var16 = false;
                    } else {
                        var16 = false;
                    }
                    break label340;
                } catch (Throwable e1) {
                    if (this.toStop) {
                        XxlJobLogger.log("<br>----------- JobThread toStop, stopReason:" + this.stopReason, new Object[0]);
                    }

                    StringWriter stringWriter = new StringWriter();
                    e1.printStackTrace(new PrintWriter(stringWriter));
                    String errorMsg = stringWriter.toString();
                    executeResult = new ReturnT(500, errorMsg);
                    XxlJobLogger.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- yh-job job execute end(error) -----------", new Object[0]);
                    var16 = false;
                } finally {
                    if (var16) {
                        if (triggerParam != null) {
                            if (!this.toStop) {
                                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
                            } else {
                                stopResult = new ReturnT(500, this.stopReason + " [job running，killed]");
                                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
                            }
                        }

                    }
                }

                if (triggerParam != null) {
                    if (!this.toStop) {
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
                    } else {
                        stopResult = new ReturnT(500, this.stopReason + " [job running，killed]");
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
                    }
                }
                continue;
            }

            if (triggerParam != null) {
                if (!this.toStop) {
                    TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
                } else {
                    stopResult = new ReturnT(500, this.stopReason + " [job running，killed]");
                    TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
                }
            }
        }

        while(this.triggerQueue != null && this.triggerQueue.size() > 0) {
            triggerParam = (TriggerParam)this.triggerQueue.poll();
            if (triggerParam != null) {
                executeResult = new ReturnT(500, this.stopReason + " [job not executed, in the job queue, killed.]");
                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
            }
        }

        try {
            this.handler.destroy();
        } catch (Throwable var23) {
            e = var23;
            log.error(e.getMessage(), e);
        }

        log.info(">>>>>>>>>>> yh-job JobThread stoped, hashCode:{}", Thread.currentThread());
    }
}
