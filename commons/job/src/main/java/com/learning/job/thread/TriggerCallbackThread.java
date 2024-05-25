package com.learning.job.thread;

import com.alibaba.fastjson.JSON;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.learning.job.biz.model.HandleCallbackParam;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.log.XxlJobFileAppender;
import com.learning.job.log.XxlJobLogger;
import com.learning.job.utils.DiscoveryUtil;
import com.learning.job.utils.FileUtil;
import com.learning.job.utils.HttpUtil;
import com.learning.job.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);
    private static TriggerCallbackThread instance = new TriggerCallbackThread();
    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue();
    private Thread triggerCallbackThread;
    private Thread triggerRetryCallbackThread;
    private volatile boolean toStop = false;
    private static String failCallbackFilePath;
    private static String failCallbackFileName;

    public TriggerCallbackThread() {
    }

    public static TriggerCallbackThread getInstance() {
        return instance;
    }

    public static void pushCallBack(HandleCallbackParam callback) {
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> yh-job, push callback request, logId:{}", callback.getLogId());
    }

    public void start() {
        this.triggerCallbackThread = new Thread(() -> {
            Exception e;
            while(!TriggerCallbackThread.this.toStop) {
                try {
                    HandleCallbackParam callback = (HandleCallbackParam)TriggerCallbackThread.getInstance().callBackQueue.take();
                    if (callback != null) {
                        List<HandleCallbackParam> callbackParamListx = new ArrayList();
                        int drainToNumx = TriggerCallbackThread.getInstance().callBackQueue.drainTo(callbackParamListx);
                        callbackParamListx.add(callback);
                        if (callbackParamListx != null && callbackParamListx.size() > 0) {
                            TriggerCallbackThread.this.doCallback(callbackParamListx);
                        }
                    }
                } catch (Exception var4) {
                    e = var4;
                    if (!TriggerCallbackThread.this.toStop) {
                        TriggerCallbackThread.logger.error(e.getMessage(), e);
                    }
                }
            }

            try {
                List<HandleCallbackParam> callbackParamList = new ArrayList();
                int drainToNum = TriggerCallbackThread.getInstance().callBackQueue.drainTo(callbackParamList);
                if (callbackParamList != null && callbackParamList.size() > 0) {
                    TriggerCallbackThread.this.doCallback(callbackParamList);
                }
            } catch (Exception var5) {
                e = var5;
                if (!TriggerCallbackThread.this.toStop) {
                    TriggerCallbackThread.logger.error(e.getMessage(), e);
                }
            }

            TriggerCallbackThread.logger.info(">>>>>>>>>>> yh-job, executor callback thread destory.");
        });
        this.triggerCallbackThread.setDaemon(true);
        this.triggerCallbackThread.setName("yh-job, executor TriggerCallbackThread");
        this.triggerCallbackThread.start();
        this.triggerRetryCallbackThread = new Thread(new Runnable() {
            public void run() {
                while(!TriggerCallbackThread.this.toStop) {
                    try {
                        TriggerCallbackThread.this.retryFailCallbackFile();
                    } catch (Exception var3) {
                        Exception e = var3;
                        if (!TriggerCallbackThread.this.toStop) {
                            TriggerCallbackThread.logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(30L);
                    } catch (InterruptedException var2) {
                        InterruptedException ex = var2;
                        if (!TriggerCallbackThread.this.toStop) {
                            TriggerCallbackThread.logger.error(ex.getMessage(), ex);
                        }
                    }
                }

                TriggerCallbackThread.logger.info(">>>>>>>>>>> yh-job, executor retry callback thread destory.");
            }
        });
        this.triggerRetryCallbackThread.setDaemon(true);
        this.triggerRetryCallbackThread.start();
    }

    public void toStop() {
        this.toStop = true;
        InterruptedException e;
        if (this.triggerCallbackThread != null) {
            this.triggerCallbackThread.interrupt();

            try {
                this.triggerCallbackThread.join();
            } catch (InterruptedException var3) {
                e = var3;
                logger.error(e.getMessage(), e);
            }
        }

        if (this.triggerRetryCallbackThread != null) {
            this.triggerRetryCallbackThread.interrupt();

            try {
                this.triggerRetryCallbackThread.join();
            } catch (InterruptedException var2) {
                e = var2;
                logger.error(e.getMessage(), e);
            }
        }

    }

    private void doCallback(List<HandleCallbackParam> callbackParamList) {
        boolean callbackRet = false;
        Map<String, List<String>> adminServicesList = DiscoveryUtil.adminServicesList;
        Iterator var4 = adminServicesList.keySet().iterator();

        while(var4.hasNext()) {
            String key = (String)var4.next();
            List<String> urls = (List)adminServicesList.get(key);
            Iterator var7 = urls.iterator();

            while(var7.hasNext()) {
                String url = (String)var7.next();

                try {
                    String result = HttpUtil.sendHttpPost("http://" + url + "/api", callbackParamList);
                    ReturnT<String> callbackResult = (ReturnT)JSON.toJavaObject(JSON.parseObject(result), ReturnT.class);
                    if (callbackResult != null && 200 == callbackResult.getCode()) {
                        this.callbackLog(callbackParamList, "<br>----------- yh-job job callback finish.");
                        callbackRet = true;
                        break;
                    }

                    this.callbackLog(callbackParamList, "<br>----------- yh-job job callback fail, callbackResult:" + callbackResult);
                } catch (Exception var11) {
                    Exception e = var11;
                    this.callbackLog(callbackParamList, "<br>----------- yh-job job callback error, errorMsg:" + e.getMessage());
                }
            }
        }

        if (!callbackRet) {
            this.appendFailCallbackFile(callbackParamList);
        }

    }

    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        Iterator var3 = callbackParamList.iterator();

        while(var3.hasNext()) {
            HandleCallbackParam callbackParam = (HandleCallbackParam)var3.next();
            String logFileName = XxlJobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
            XxlJobFileAppender.contextHolder.set(logFileName);
            XxlJobLogger.log(logContent);
        }

    }

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList) {
        if (callbackParamList != null && callbackParamList.size() != 0) {
            byte[] callbackParamList_bytes = SerializeUtil.serialize(callbackParamList);
            File callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis())));
            if (callbackLogFile.exists()) {
                for(int i = 0; i < 100; ++i) {
                    callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis()).concat("-").concat(String.valueOf(i))));
                    if (!callbackLogFile.exists()) {
                        break;
                    }
                }
            }

            FileUtil.writeFileContent(callbackLogFile, callbackParamList_bytes);
        }
    }

    private void retryFailCallbackFile() throws Exception {
        File callbackLogPath = new File(failCallbackFilePath);
        if (callbackLogPath.exists()) {
            if (callbackLogPath.isFile()) {
                callbackLogPath.delete();
            }

            if (callbackLogPath.isDirectory() && callbackLogPath.list() != null && callbackLogPath.list().length > 0) {
                File[] var2 = callbackLogPath.listFiles();
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    File callbaclLogFile = var2[var4];
                    if (callbaclLogFile.exists()) {
                        try {
                            byte[] callbackParamList_bytes = FileUtil.readFileContent(callbaclLogFile);
                            List<HandleCallbackParam> list = SerializeUtil.deserialize(callbackParamList_bytes, List.class);
                            List<HandleCallbackParam> callbackParamList = JSON.parseArray(JSON.toJSONString(list), HandleCallbackParam.class);
                            callbaclLogFile.delete();
                            this.doCallback(callbackParamList);
                        } catch (Exception var9) {
                            logger.error("删除或doCallback：{}失败！", callbaclLogFile.getName());
                        }
                    }
                }

            }
        }
    }

    static {
        failCallbackFilePath = XxlJobFileAppender.getLogPath().concat(File.separator).concat(XxlJobFileAppender.callBackLog).concat(File.separator);
        failCallbackFileName = failCallbackFilePath.concat("yh-job-callback-{x}").concat(".log");
    }
}
