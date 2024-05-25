package com.learning.job.schedule.core.thread;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JobFailMonitorHelper {

    private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
    private Thread monitorThread;
    private volatile boolean toStop = false;
    private static final String mailBodyTemplate = "<h5>" + I18nUtil.getString("jobconf_monitor_detail") + "：</span><table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >      <tr>\n         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobgroup") + "</td>\n         <td width=\"10%\" >" + I18nUtil.getString("jobinfo_field_id") + "</td>\n         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobdesc") + "</td>\n         <td width=\"10%\" >" + I18nUtil.getString("jobconf_monitor_alarm_title") + "</td>\n         <td width=\"40%\" >" + I18nUtil.getString("jobconf_monitor_alarm_content") + "</td>\n      </tr>\n   </thead>\n   <tbody>\n      <tr>\n         <td>{0}</td>\n         <td>{1}</td>\n         <td>{2}</td>\n         <td>" + I18nUtil.getString("jobconf_monitor_alarm_type") + "</td>\n         <td>{3}</td>\n      </tr>\n   </tbody>\n</table>";
    private static final String dingTalkBodyTemplate = I18nUtil.getString("jobconf_monitor_detail") + ":\n" + I18nUtil.getString("jobinfo_field_jobgroup") + ":%s\n" + I18nUtil.getString("jobinfo_field_id") + ":%s\n" + I18nUtil.getString("jobinfo_field_jobdesc") + ":%s\n" + I18nUtil.getString("jobconf_monitor_alarm_title") + ":" + I18nUtil.getString("jobconf_monitor_alarm_type") + "\n" + I18nUtil.getString("jobconf_monitor_alarm_content") + ":%s";

    public JobFailMonitorHelper() {
    }

    public static JobFailMonitorHelper getInstance() {
        return instance;
    }

    public void start() {
        this.monitorThread = new Thread(new Runnable() {
            public void run() {
                while(!JobFailMonitorHelper.this.toStop) {
                    try {
                        List<Long> failLogIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findFailJobLogIds(1000);
                        if (failLogIds != null && !failLogIds.isEmpty()) {
                            Iterator var2 = failLogIds.iterator();

                            while(var2.hasNext()) {
                                long failLogId = (Long)var2.next();
                                int lockRet = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, 0, -1);
                                if (lockRet >= 1) {
                                    XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(failLogId);
                                    XxlJobInfo info = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(log.getJobId());
                                    if (log.getExecutorFailRetryCount() > 0) {
                                        JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, log.getExecutorFailRetryCount() - 1, log.getExecutorShardingParam(), log.getExecutorParam(), (String)null);
                                        String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_type_retry") + "<<<<<<<<<<< </span><br>";
                                        log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
                                        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(log);
                                    }

                                    int newAlarmStatus = false;
                                    int newAlarmStatusx;
                                    if (info != null) {
                                        boolean alarmResult = true;

                                        try {
                                            alarmResult = JobFailMonitorHelper.this.failAlarm(info, log);
                                        } catch (Exception var11) {
                                            Exception ex = var11;
                                            alarmResult = false;
                                            JobFailMonitorHelper.logger.error(ex.getMessage(), ex);
                                        }

                                        newAlarmStatusx = alarmResult ? 2 : 3;
                                    } else {
                                        newAlarmStatusx = 1;
                                    }

                                    XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateAlarmStatus(failLogId, -1, newAlarmStatusx);
                                }
                            }
                        }

                        TimeUnit.SECONDS.sleep(10L);
                    } catch (Exception var12) {
                        Exception e = var12;
                        e.printStackTrace();
                        if (!JobFailMonitorHelper.this.toStop) {
                            JobFailMonitorHelper.logger.error(">>>>>>>>>>> yh-job, job fail monitor thread error:{}", e);
                        }
                    }
                }

                JobFailMonitorHelper.logger.info(">>>>>>>>>>> yh-job, job fail monitor thread stop");
            }
        });
        this.monitorThread.setDaemon(true);
        this.monitorThread.setName("yh-job, admin JobFailMonitorHelper");
        this.monitorThread.start();
    }

    public void toStop() {
        this.toStop = true;
        this.monitorThread.interrupt();

        try {
            this.monitorThread.join();
        } catch (InterruptedException var2) {
            InterruptedException e = var2;
            logger.error(e.getMessage(), e);
        }

    }

    private boolean failAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;
        XxlJobGroup group;
        String personal;
        String title;
        String content;
        String url;
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != 200) {
                alarmContent = alarmContent + "<br>TriggerMsg=<br>" + jobLog.getTriggerMsg();
            }

            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != 200) {
                alarmContent = alarmContent + "<br>HandleMsg=<br>" + jobLog.getHandleMsg();
            }

            group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            personal = I18nUtil.getString("admin_name_full");
            title = I18nUtil.getString("jobconf_monitor");
            content = MessageFormat.format(mailBodyTemplate, group != null ? group.getTitle() : "null", info.getId(), info.getJobDesc(), alarmContent);
            Set<String> emailSet = new HashSet(Arrays.asList(info.getAlarmEmail().split(",")));
            Iterator var10 = emailSet.iterator();

            while(var10.hasNext()) {
                url = (String)var10.next();

                try {
                    MimeMessage mimeMessage = XxlJobAdminConfig.getAdminConfig().getMailSender().createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    helper.setFrom(XxlJobAdminConfig.getAdminConfig().getEmailUserName(), personal);
                    helper.setTo(url);
                    helper.setSubject(title);
                    helper.setText(content, true);
                    XxlJobAdminConfig.getAdminConfig().getMailSender().send(mimeMessage);
                } catch (Exception var15) {
                    Exception e = var15;
                    logger.error(">>>>>>>>>>> yh-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);
                    alarmResult = false;
                }
            }
        }

        if (!XxlJobAdminConfig.getAdminConfig().isDingEnable()) {
            return alarmResult;
        } else {
            DingTalkMessageDTO dingTalkMessageDTO = new DingTalkMessageDTO();
            group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            personal = "\n Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != 200) {
                personal = personal + "\n TriggerMsg=" + jobLog.getTriggerMsg();
            }

            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != 200) {
                personal = personal + "\n HandleMsg=" + jobLog.getHandleMsg();
            }

            title = String.format(dingTalkBodyTemplate, group != null ? group.getTitle() : "null", info.getId(), info.getJobDesc(), personal);
            dingTalkMessageDTO.setContent(title);
            dingTalkMessageDTO.setMsgType(1);
            dingTalkMessageDTO.setRobotId(XxlJobAdminConfig.getAdminConfig().getRobotId());
            content = XxlJobAdminConfig.getAdminConfig().getAtList();
            String[] split = content.split(",");
            List<String> atList = Arrays.asList(split);
            dingTalkMessageDTO.setAtAll(XxlJobAdminConfig.getAdminConfig().isAtAll());
            dingTalkMessageDTO.setAtList(atList);

            try {
                url = XxlJobAdminConfig.getAdminConfig().getUrl();
                logger.info("发送钉钉预警消息请求>>>>>>开始,请求地址url:{},请求参数:{}", url, JSON.toJSONString(dingTalkMessageDTO));
                ResponseEntity<PublicResult> result = XxlJobAdminConfig.getAdminConfig().getRestTemplate().postForEntity(url, dingTalkMessageDTO, PublicResult.class, new Object[0]);
                logger.info("发送钉钉预警消息请求>>>>>>结束,返回结果:{}", JSON.toJSONString(result));
                alarmResult = true;
            } catch (Exception var14) {
                Exception e = var14;
                logger.error(">>>>>>>>>>> yh-job, job fail alarm dingTalk send error, JobLogId:{}", jobLog.getId(), e);
                alarmResult = false;
            }

            return alarmResult;
        }
    }
}
