package com.learning.job.schedule.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.glue.GlueTypeEnum;
import com.learning.job.schedule.consts.JobConst;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobLogGlue;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.XxlJobInfoDao;
import com.learning.job.schedule.dao.XxlJobLogGlueDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/jobcode"})
public class JobCodeController {
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;

    public JobCodeController() {
    }

    @RequestMapping
    @ResponseBody
    public ReturnT<Map<String, Object>> index(HttpServletRequest request, int jobId) {
        XxlJobInfo jobInfo = this.xxlJobInfoDao.loadById(jobId);
        List<XxlJobLogGlue> jobLogGlues = this.xxlJobLogGlueDao.findByJobId(jobId);
        if (jobInfo == null) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        } else if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
        } else {
            JobInfoController.validPermission(request, jobInfo.getJobGroup());
            Map<String, Object> map = new HashMap();
            map.put("GlueTypeEnum", JobConst.GLUE_TYPE_ENUM);
            map.put("jobInfo", jobInfo);
            map.put("jobLogGlues", jobLogGlues);
            return new ReturnT(map);
        }
    }

    @RequestMapping({"/save"})
    @ResponseBody
    public ReturnT<String> save(Model model, int id, String glueSource, String glueRemark) {
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
                xxlJobLogGlue.setAddTime(new Date());
                xxlJobLogGlue.setUpdateTime(new Date());
                this.xxlJobLogGlueDao.save(xxlJobLogGlue);
                this.xxlJobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);
                return ReturnT.SUCCESS;
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
        }
    }
}

