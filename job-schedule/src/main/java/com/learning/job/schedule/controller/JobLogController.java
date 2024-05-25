package com.learning.job.schedule.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.learning.job.biz.ExecutorBiz;
import com.learning.job.biz.model.LogResult;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.core.conf.XxlJobScheduler;
import com.learning.job.schedule.core.exception.XxlJobException;
import com.learning.job.schedule.core.model.XxlJobCompleteInfo;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobLog;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.XxlJobGroupDao;
import com.learning.job.schedule.dao.XxlJobInfoDao;
import com.learning.job.schedule.dao.XxlJobLogDao;
import com.learning.job.utils.DateUtil;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/joblog"})
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;

    public JobLogController() {
    }

    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false,defaultValue = "0") Integer jobId) {
        List<XxlJobGroup> jobGroupList_all = this.xxlJobGroupDao.findAll();
        List<XxlJobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
        if (jobGroupList != null && jobGroupList.size() != 0) {
            model.addAttribute("JobGroupList", jobGroupList);
            if (jobId > 0) {
                XxlJobInfo jobInfo = this.xxlJobInfoDao.loadById(jobId);
                if (jobInfo == null) {
                    throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
                }

                model.addAttribute("jobInfo", jobInfo);
                JobInfoController.validPermission(request, jobInfo.getJobGroup());
            }

            return "joblog/joblog.index";
        } else {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }
    }

    @RequestMapping({"/getJobsByGroup"})
    @ResponseBody
    public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
        List<XxlJobInfo> list = this.xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ReturnT(list);
    }

    @RequestMapping({"/pageList"})
    @ResponseBody
    public ReturnT<Map<String, Object>> pageList(HttpServletRequest request, @RequestParam(required = false,defaultValue = "0") int start, @RequestParam(required = false,defaultValue = "10") int length, int jobGroup, int jobId, int logStatus, String filterTime) {
        JobInfoController.validPermission(request, jobGroup);
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (filterTime != null && filterTime.trim().length() > 0) {
            String[] temp = filterTime.split(" - ");
            if (temp != null && temp.length == 2) {
                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
            }
        }

        List<XxlJobLog> list = this.xxlJobLogDao.pageList(start == 0 ? 0 : start * length, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int list_count = this.xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        Map<String, Object> maps = new HashMap();
        maps.put("recordsTotal", list_count);
        maps.put("recordsFiltered", list_count);
        maps.put("data", list);
        return new ReturnT(maps);
    }

    @RequestMapping({"/logDetailPage"})
    public String logDetailPage(int id, Model model) {
        ReturnT<String> logStatue = ReturnT.SUCCESS;
        XxlJobLog jobLog = this.xxlJobLogDao.load((long)id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        } else {
            model.addAttribute("triggerCode", jobLog.getTriggerCode());
            model.addAttribute("handleCode", jobLog.getHandleCode());
            model.addAttribute("executorAddress", jobLog.getExecutorAddress());
            model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
            model.addAttribute("logId", jobLog.getId());
            return "joblog/joblog.detail";
        }
    }

    @RequestMapping({"/logDetailCat"})
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum) {
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(triggerTime, logId, fromLineNum);
            if (logResult.getContent() != null && ((LogResult)logResult.getContent()).getFromLineNum() > ((LogResult)logResult.getContent()).getToLineNum()) {
                XxlJobLog jobLog = this.xxlJobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                    ((LogResult)logResult.getContent()).setEnd(true);
                }
            }

            return logResult;
        } catch (Exception var10) {
            Exception e = var10;
            logger.error(e.getMessage(), e);
            return new ReturnT(500, e.getMessage());
        }
    }

    @RequestMapping({"/logKill"})
    @ResponseBody
    public ReturnT<String> logKill(int id) {
        XxlJobLog log = this.xxlJobLogDao.load((long)id);
        XxlJobInfo jobInfo = this.xxlJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        } else if (200 != log.getTriggerCode()) {
            return new ReturnT(500, I18nUtil.getString("joblog_kill_log_limit"));
        } else {
            ReturnT<String> runResult = null;

            try {
                ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
                runResult = executorBiz.kill(jobInfo.getId());
            } catch (Exception var6) {
                Exception e = var6;
                logger.error(e.getMessage(), e);
                runResult = new ReturnT(500, e.getMessage());
            }

            if (200 == runResult.getCode()) {
                log.setHandleCode(500);
                log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
                log.setHandleTime(new Date());
                this.xxlJobLogDao.updateHandleInfo(log);
                return new ReturnT(runResult.getMsg());
            } else {
                return new ReturnT(500, runResult.getMsg());
            }
        }
    }

    @RequestMapping({"/clearLog"})
    @ResponseBody
    public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {
        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1);
        } else if (type == 5) {
            clearBeforeNum = 1000;
        } else if (type == 6) {
            clearBeforeNum = 10000;
        } else if (type == 7) {
            clearBeforeNum = 30000;
        } else if (type == 8) {
            clearBeforeNum = 100000;
        } else {
            if (type != 9) {
                return new ReturnT(500, I18nUtil.getString("joblog_clean_type_unvalid"));
            }

            clearBeforeNum = 0;
        }

        List<Long> logIds = null;

        do {
            logIds = this.xxlJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                this.xxlJobLogDao.clearLog(logIds);
            }
        } while(logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }

    @RequestMapping({"/completeLog"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> completeLog(@RequestBody XxlJobCompleteInfo info) {
        if (null == info) {
            return new ReturnT(500, "参数无效");
        } else {
            XxlJobLog jobLog = new XxlJobLog();
            jobLog.setId(info.getId());
            jobLog.setHandleTime(new Date());
            jobLog.setHandleCode(0);
            if (BooleanUtils.isTrue(info.isSuccess())) {
                jobLog.setHandleCode(200);
            }

            jobLog.setHandleMsg(info.getHandleMsg());
            this.xxlJobLogDao.updateHandleInfo(jobLog);
            return ReturnT.SUCCESS;
        }
    }
}

