package com.learning.job.thread;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.learning.job.log.XxlJobFileAppender;
import com.learning.job.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogFileCleanThread {
    private static Logger logger = LoggerFactory.getLogger(JobLogFileCleanThread.class);
    private static JobLogFileCleanThread instance = new JobLogFileCleanThread();
    private Thread localThread;
    private volatile boolean toStop = false;

    public JobLogFileCleanThread() {
    }

    public static JobLogFileCleanThread getInstance() {
        return instance;
    }

    public void start(final long logRetentionDays) {
        if (logRetentionDays >= 3L) {
            this.localThread = new Thread(() -> {
                while(!JobLogFileCleanThread.this.toStop) {
                    try {
                        File[] childDirs = (new File(XxlJobFileAppender.getLogPath())).listFiles();
                        if (childDirs != null && childDirs.length > 0) {
                            Calendar todayCal = Calendar.getInstance();
                            todayCal.set(11, 0);
                            todayCal.set(12, 0);
                            todayCal.set(13, 0);
                            todayCal.set(14, 0);
                            Date todayDate = todayCal.getTime();
                            File[] var4 = childDirs;
                            int var5 = childDirs.length;

                            for(int var6 = 0; var6 < var5; ++var6) {
                                File childFile = var4[var6];
                                if (childFile.isDirectory() && childFile.getName().indexOf("-") != -1) {
                                    Date logFileCreateDate = null;

                                    try {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        logFileCreateDate = simpleDateFormat.parse(childFile.getName());
                                    } catch (ParseException var11) {
                                        ParseException ex = var11;
                                        JobLogFileCleanThread.logger.error(ex.getMessage(), ex);
                                    }

                                    if (logFileCreateDate != null && todayDate.getTime() - logFileCreateDate.getTime() >= logRetentionDays * 86400000L) {
                                        FileUtil.deleteRecursively(childFile);
                                    }
                                }
                            }
                        }
                    } catch (Exception var12) {
                        Exception e = var12;
                        if (!JobLogFileCleanThread.this.toStop) {
                            JobLogFileCleanThread.logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        TimeUnit.DAYS.sleep(1L);
                    } catch (InterruptedException var10) {
                        if (!JobLogFileCleanThread.this.toStop) {
                            JobLogFileCleanThread.logger.error(var10.getMessage(), var10);
                        }
                    }
                }

                JobLogFileCleanThread.logger.info(">>>>>>>>>>> yh-job, executor JobLogFileCleanThread thread destory.");
            });
            this.localThread.setDaemon(true);
            this.localThread.setName("yh-job, executor JobLogFileCleanThread");
            this.localThread.start();
        }
    }

    public void toStop() {
        this.toStop = true;
        if (this.localThread != null) {
            this.localThread.interrupt();

            try {
                this.localThread.join();
            } catch (InterruptedException var2) {
                InterruptedException e = var2;
                logger.error(e.getMessage(), e);
            }

        }
    }
}
