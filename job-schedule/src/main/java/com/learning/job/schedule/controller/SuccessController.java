package com.learning.job.schedule.controller;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.glue.GlueTypeEnum;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobLog;
import com.learning.job.schedule.core.model.XxlJobLogGlue;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.XxlJobInfoDao;
import com.learning.job.schedule.dao.XxlJobLogDao;
import com.learning.job.schedule.dao.XxlJobLogGlueDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping({"/jobcode/v2"})
public class JobCodeV2Controller {
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;
    @Resource
    private XxlJobLogDao xxlJobLogDao;

    public JobCodeV2Controller() {
    }

    @RequestMapping({"/test"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public void test() {
        for(int i = 0; i < 100000; ++i) {
            XxlJobLog xxlJobLog = new XxlJobLog();
            xxlJobLog.setJobGroup(30);
            xxlJobLog.setJobId(392);
            xxlJobLog.setExecutorAddress("192.168.224.80:8082");
            xxlJobLog.setExecutorHandler("demoJobHandler");
            xxlJobLog.setExecutorFailRetryCount(0);
            xxlJobLog.setTriggerTime(new Date());
            xxlJobLog.setTriggerCode(200);
            xxlJobLog.setTriggerMsg("任务触发类型：手动触发<br>调度机器：192.168.125.80:8082<br>执行器-注册方式：自动注册<br>执行器-地址列表：[192.168.125.80:8082]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：192.168.125.80:8082<br>code：100<br>msg：null");
            xxlJobLog.setHandleTime(new Date());
            xxlJobLog.setHandleCode(200);
            xxlJobLog.setAlarmStatus(0);
            this.xxlJobLogDao.save(xxlJobLog);
        }

    }

    @RequestMapping
    public String index(HttpServletRequest request, Model model, int jobId) {
        XxlJobInfo jobInfo = this.xxlJobInfoDao.loadById(jobId);
        List<XxlJobLogGlue> jobLogGlues = this.xxlJobLogGlueDao.findByJobId(jobId);
        if (jobInfo == null) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        } else if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
        } else {
            JobInfoController.validPermission(request, jobInfo.getJobGroup());
            model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());
            model.addAttribute("jobInfo", jobInfo);
            model.addAttribute("jobLogGlues", jobLogGlues);
            return "jobcode/jobcode.index";
        }
    }

    @RequestMapping({"/save"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> save(@RequestParam int id, @RequestParam String glueSource, @RequestParam String glueRemark) {
        if (glueRemark == null) {
            return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark"));
        } else if (glueRemark.length() >= 4 && glueRemark.length() <= 100) {
            XxlJobInfo exists_jobInfo = this.xxlJobInfoDao.loadById(id);
            if (exists_jobInfo == null) {
                return new ReturnT(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
            } else {
                exists_jobInfo.setGlueSource(glueSource);
                exists_jobInfo.setGlueRemark(glueRemark);
                exists_jobInfo.setGlueUpdateTime(new Date());
                this.xxlJobInfoDao.update(exists_jobInfo);
                XxlJobLogGlue xxlJobLogGlue = new XxlJobLogGlue();
                xxlJobLogGlue.setJobId(exists_jobInfo.getId());
                xxlJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
                xxlJobLogGlue.setGlueSource(glueSource);
                xxlJobLogGlue.setGlueRemark(glueRemark);
                this.xxlJobLogGlueDao.save(xxlJobLogGlue);
                this.xxlJobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);
                return ReturnT.SUCCESS;
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
        }
    }

    @RequestMapping({"/checkCron"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public PublicResult checkCron(@RequestParam String cron) {
        return PublicResult.buildQuerySucess(CronExpression.isValidExpression(cron));
    }
}