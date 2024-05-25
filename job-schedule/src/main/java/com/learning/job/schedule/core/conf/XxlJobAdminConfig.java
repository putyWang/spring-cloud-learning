package com.learning.job.schedule.core.conf;

import com.learning.job.biz.AdminBiz;
import com.learning.job.schedule.dao.XxlJobGroupDao;
import com.learning.job.schedule.dao.XxlJobInfoDao;
import com.learning.job.schedule.dao.XxlJobLogDao;
import com.learning.job.schedule.dao.XxlJobRegistryDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Component
public class XxlJobAdminConfig implements InitializingBean {

    private static XxlJobAdminConfig adminConfig = null;
    @Value("${xxl.job.i18n}")
    private String i18n;
    @Value("${xxl.job.accessToken}")
    private String accessToken;
    @Value("${spring.mail.username}")
    private String emailUserName;
    @Value("200")
    private int triggerPoolFastMax;
    @Value("100")
    private int triggerPoolSlowMax;
    @Value("${spring.application.name}")
    private String name;
    @Value("${fail-alarm.ding.enable:false}")
    private boolean dingEnable;
    @Value("${fail-alarm.ding.robotId:}")
    private String robotId;
    @Value("${fail-alarm.ding.atAll:true}")
    private boolean atAll;
    @Value("${fail-alarm.ding.atList:}")
    private String atList;
    @Value("${fail-alarm.ding.url:}")
    private String url;
    @Resource
    private XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private AdminBiz adminBiz;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private DataSource dataSource;
    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private RestTemplate restTemplate;

    public XxlJobAdminConfig() {
    }

    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }

    public void afterPropertiesSet() throws Exception {
        adminConfig = this;
    }

    public int getTriggerPoolFastMax() {
        return this.triggerPoolFastMax < 100 ? 100 : this.triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        return this.triggerPoolSlowMax < 100 ? 100 : this.triggerPoolSlowMax;
    }

    public String getI18n() {
        return this.i18n;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getEmailUserName() {
        return this.emailUserName;
    }

    public XxlJobLogDao getXxlJobLogDao() {
        return this.xxlJobLogDao;
    }

    public XxlJobInfoDao getXxlJobInfoDao() {
        return this.xxlJobInfoDao;
    }

    public XxlJobRegistryDao getXxlJobRegistryDao() {
        return this.xxlJobRegistryDao;
    }

    public XxlJobGroupDao getXxlJobGroupDao() {
        return this.xxlJobGroupDao;
    }

    public XxlJobService xxlJobService() {
        return this.xxlJobService;
    }

    public AdminBiz getAdminBiz() {
        return this.adminBiz;
    }

    public JavaMailSender getMailSender() {
        return this.mailSender;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDingEnable() {
        return this.dingEnable;
    }

    public String getRobotId() {
        return this.robotId;
    }

    public boolean isAtAll() {
        return this.atAll;
    }

    public String getAtList() {
        return this.atList;
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    public String getUrl() {
        return this.url;
    }
}
