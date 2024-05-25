package com.learning.job.schedule.core.thread;

import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.learning.job.schedule.core.conf.XxlJobAdminConfig;
import com.learning.job.schedule.core.exception.XxlJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduleHelper {
    private static Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);
    private static JobScheduleHelper instance = new JobScheduleHelper();
    public static final long PRE_READ_MS = 5000L;
    public static final String SQLSERVER_SQL = "select * from xxl_job_lock WITH (UPDLOCK)  where lock_name = 'schedule_lock'";
    public static final String POSTGRE_SQL = "select * from xxl_job_lock where lock_name = 'schedule_lock' for update";
    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private static volatile Map<Integer, List<Integer>> ringData = new ConcurrentHashMap();

    public JobScheduleHelper() {
    }

    public static JobScheduleHelper getInstance() {
        return instance;
    }

    public void start() {
        this.scheduleThread = new Thread(new Runnable() {
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000L - System.currentTimeMillis() % 1000L);
                } catch (InterruptedException var40) {
                    InterruptedException e = var40;
                    if (!JobScheduleHelper.this.scheduleThreadToStop) {
                        JobScheduleHelper.logger.error(e.getMessage(), e);
                    }
                }

                JobScheduleHelper.logger.info(">>>>>>>>> init yh-job admin scheduler success.");

                while(!JobScheduleHelper.this.scheduleThreadToStop) {
                    long start = System.currentTimeMillis();
                    Connection conn = null;
                    Boolean connAutoCommit = null;
                    PreparedStatement preparedStatement = null;
                    boolean preReadSuc = true;

                    try {
                        conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
                        connAutoCommit = conn.getAutoCommit();
                        conn.setAutoCommit(false);
                        String databaseProductName = conn.getMetaData().getDatabaseProductName();
                        String sql;
                        if ("PostgreSQL".equals(databaseProductName)) {
                            sql = "select * from xxl_job_lock where lock_name = 'schedule_lock' for update";
                        } else {
                            if (!"Microsoft SQL Server".equals(databaseProductName)) {
                                throw new XxlJobException("不支持的数据库类型=" + databaseProductName);
                            }

                            sql = "select * from xxl_job_lock WITH (UPDLOCK)  where lock_name = 'schedule_lock'";
                        }

                        preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.execute();
                        long nowTime = System.currentTimeMillis();
                        List<XxlJobInfo> scheduleList = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleJobQuery(nowTime + 5000L);
                        if (scheduleList != null && scheduleList.size() > 0) {
                            Iterator var12 = scheduleList.iterator();

                            XxlJobInfo jobInfo;
                            while(var12.hasNext()) {
                                jobInfo = (XxlJobInfo)var12.next();
                                if (nowTime > jobInfo.getTriggerNextTime() + 5000L) {
                                    JobScheduleHelper.logger.warn(">>>>>>>>>>> yhjob, schedule misfire, jobId = " + jobInfo.getId());
                                    JobScheduleHelper.this.refreshNextValidTime(jobInfo, new Date());
                                } else {
                                    int ringSecond;
                                    if (nowTime > jobInfo.getTriggerNextTime()) {
                                        JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, (String)null, (String)null, (String)null);
                                        JobScheduleHelper.logger.debug(">>>>>>>>>>> yh-job, schedule push trigger : jobId = " + jobInfo.getId());
                                        JobScheduleHelper.this.refreshNextValidTime(jobInfo, new Date());
                                        if (jobInfo.getTriggerStatus() == 1 && nowTime + 5000L > jobInfo.getTriggerNextTime()) {
                                            ringSecond = (int)(jobInfo.getTriggerNextTime() / 1000L % 60L);
                                            JobScheduleHelper.this.pushTimeRing(ringSecond, jobInfo.getId());
                                            JobScheduleHelper.this.refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                        }
                                    } else {
                                        ringSecond = (int)(jobInfo.getTriggerNextTime() / 1000L % 60L);
                                        JobScheduleHelper.this.pushTimeRing(ringSecond, jobInfo.getId());
                                        JobScheduleHelper.this.refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                    }
                                }
                            }

                            var12 = scheduleList.iterator();

                            while(var12.hasNext()) {
                                jobInfo = (XxlJobInfo)var12.next();
                                XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleUpdate(jobInfo);
                            }
                        } else {
                            preReadSuc = false;
                        }
                    } catch (Exception var38) {
                        Exception exx = var38;
                        exx.printStackTrace();
                        if (!JobScheduleHelper.this.scheduleThreadToStop) {
                            JobScheduleHelper.logger.error(">>>>>>>>>>> yh-job, JobScheduleHelper#scheduleThread error:{}", exx);
                        }
                    } finally {
                        SQLException exxx;
                        if (conn != null) {
                            try {
                                conn.commit();
                            } catch (SQLException var36) {
                                exxx = var36;
                                if (!JobScheduleHelper.this.scheduleThreadToStop) {
                                    JobScheduleHelper.logger.error(exxx.getMessage(), exxx);
                                }
                            }

                            try {
                                conn.setAutoCommit(connAutoCommit);
                            } catch (SQLException var35) {
                                exxx = var35;
                                if (!JobScheduleHelper.this.scheduleThreadToStop) {
                                    JobScheduleHelper.logger.error(exxx.getMessage(), exxx);
                                }
                            }

                            try {
                                conn.close();
                            } catch (SQLException var34) {
                                exxx = var34;
                                if (!JobScheduleHelper.this.scheduleThreadToStop) {
                                    JobScheduleHelper.logger.error(exxx.getMessage(), exxx);
                                }
                            }
                        }

                        if (null != preparedStatement) {
                            try {
                                preparedStatement.close();
                            } catch (SQLException var33) {
                                exxx = var33;
                                if (!JobScheduleHelper.this.scheduleThreadToStop) {
                                    JobScheduleHelper.logger.error(exxx.getMessage(), exxx);
                                }
                            }
                        }

                    }

                    long cost = System.currentTimeMillis() - start;
                    if (cost < 1000L) {
                        try {
                            TimeUnit.MILLISECONDS.sleep((preReadSuc ? 1000L : 5000L) - System.currentTimeMillis() % 1000L);
                        } catch (InterruptedException var37) {
                            InterruptedException ex = var37;
                            if (!JobScheduleHelper.this.scheduleThreadToStop) {
                                JobScheduleHelper.logger.error(ex.getMessage(), ex);
                            }
                        }
                    }
                }

                JobScheduleHelper.logger.info(">>>>>>>>>>> yh-job, JobScheduleHelper#scheduleThread stop");
            }
        });
        this.scheduleThread.setDaemon(true);
        this.scheduleThread.setName("yh-job, admin JobScheduleHelper#scheduleThread");
        this.scheduleThread.start();
        this.ringThread = new Thread(new Runnable() {
            public void run() {
                InterruptedException e;
                try {
                    TimeUnit.MILLISECONDS.sleep(1000L - System.currentTimeMillis() % 1000L);
                } catch (InterruptedException var7) {
                    e = var7;
                    if (!JobScheduleHelper.this.ringThreadToStop) {
                        JobScheduleHelper.logger.error(e.getMessage(), e);
                    }
                }

                while(!JobScheduleHelper.this.ringThreadToStop) {
                    try {
                        List<Integer> ringItemData = new ArrayList();
                        int nowSecond = Calendar.getInstance().get(13);

                        for(int i = 0; i < 2; ++i) {
                            List<Integer> tmpData = (List)JobScheduleHelper.ringData.remove((nowSecond + 60 - i) % 60);
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }

                        JobScheduleHelper.logger.debug(">>>>>>>>>>> yh-job, time-ring beat : " + nowSecond + " = " + Arrays.asList(ringItemData));
                        if (ringItemData.size() > 0) {
                            Iterator var10 = ringItemData.iterator();

                            while(var10.hasNext()) {
                                int jobId = (Integer)var10.next();
                                JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, (String)null, (String)null, (String)null);
                            }

                            ringItemData.clear();
                        }
                    } catch (Exception var6) {
                        var6.printStackTrace();
                        if (!JobScheduleHelper.this.ringThreadToStop) {
                            JobScheduleHelper.logger.error(">>>>>>>>>>> yh-job, JobScheduleHelper#ringThread error:{}", var6);
                        }
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(1000L - System.currentTimeMillis() % 1000L);
                    } catch (InterruptedException var5) {
                        e = var5;
                        if (!JobScheduleHelper.this.ringThreadToStop) {
                            JobScheduleHelper.logger.error(e.getMessage(), e);
                        }
                    }
                }

                JobScheduleHelper.logger.info(">>>>>>>>>>> yh-job, JobScheduleHelper#ringThread stop");
            }
        });
        this.ringThread.setDaemon(true);
        this.ringThread.setName("yh-job, admin JobScheduleHelper#ringThread");
        this.ringThread.start();
    }

    private void refreshNextValidTime(XxlJobInfo jobInfo, Date fromTime) throws ParseException {
        Date nextValidTime = (new CronExpression(jobInfo.getJobCron())).getNextValidTimeAfter(fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime.getTime());
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0L);
            jobInfo.setTriggerNextTime(0L);
        }

    }

    private void pushTimeRing(int ringSecond, int jobId) {
        List<Integer> ringItemData = (List)ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList();
            ringData.put(ringSecond, ringItemData);
        }

        ((List)ringItemData).add(jobId);
        logger.debug(">>>>>>>>>>> yh-job, schedule push time-ring : " + ringSecond + " = " + Arrays.asList((List)ringItemData));
    }

    public void toStop() {
        this.scheduleThreadToStop = true;

        InterruptedException e;
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException var9) {
            e = var9;
            logger.error(e.getMessage(), e);
        }

        if (this.scheduleThread.getState() != State.TERMINATED) {
            this.scheduleThread.interrupt();

            try {
                this.scheduleThread.join();
            } catch (InterruptedException var8) {
                e = var8;
                logger.error(e.getMessage(), e);
            }
        }

        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            Iterator var2 = ringData.keySet().iterator();

            while(var2.hasNext()) {
                int second = (Integer)var2.next();
                List<Integer> tmpData = (List)ringData.get(second);
                if (tmpData != null && tmpData.size() > 0) {
                    hasRingData = true;
                    break;
                }
            }
        }

        InterruptedException e;
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8L);
            } catch (InterruptedException var7) {
                e = var7;
                logger.error(e.getMessage(), e);
            }
        }

        this.ringThreadToStop = true;

        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException var6) {
            e = var6;
            logger.error(e.getMessage(), e);
        }

        if (this.ringThread.getState() != State.TERMINATED) {
            this.ringThread.interrupt();

            try {
                this.ringThread.join();
            } catch (InterruptedException var5) {
                e = var5;
                logger.error(e.getMessage(), e);
            }
        }

        logger.info(">>>>>>>>>>> yh-job, JobScheduleHelper stop");
    }
}

