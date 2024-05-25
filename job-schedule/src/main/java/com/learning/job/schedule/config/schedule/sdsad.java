package com.learning.job.schedule.config.schedule;

import com.learning.job.schedule.dao.XxlJobLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Component
public class AutoDeleteLogsController {
    private Logger logger = LoggerFactory.getLogger(AutoDeleteLogsController.class);
    @Value("${admin.job.log.keepDays:90}")
    private int keepDays;
    @Resource
    private XxlJobLogDao logDao;

    public AutoDeleteLogsController() {
    }

    @Scheduled(
            cron = "0 0 0 * * ?"
    )
    public void deleteLogs() {
        this.logger.info("开始删除日志文件，配置为--->admin.job.log.keepDays ：" + this.keepDays);
        if (this.keepDays > 0) {
            try {
                this.logger.info("begin delete {} days ago logs!", this.keepDays);
                Calendar cal = Calendar.getInstance();
                cal.add(5, -this.keepDays);
                Date date = cal.getTime();
                this.logDao.deleteLogs(date);
                this.logger.info("delete job logs success!,expireTime:" + date);
            } catch (Exception var3) {
                Exception e = var3;
                this.logger.error("delete logs error!", e);
            }
        }

    }
}
