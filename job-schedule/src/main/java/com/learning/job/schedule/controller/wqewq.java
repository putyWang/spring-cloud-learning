package com.learning.job.schedule.controller;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.consts.JobConst;
import com.learning.job.schedule.core.exception.XxlJobException;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobInfoDto;
import com.learning.job.schedule.core.model.XxlJobUser;
import com.learning.job.schedule.core.thread.JobTriggerPoolHelper;
import com.learning.job.schedule.core.trigger.TriggerTypeEnum;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.core.utils.TokenUtil;
import com.learning.job.schedule.dao.XxlJobGroupDao;
import com.learning.job.schedule.service.XxlJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping({"/jobinfo"})
public class JobInfoController {
    private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobService xxlJobService;

    @RequestMapping
    @ResponseBody
    public ReturnT<Map<String, Object>> index(HttpServletRequest request, Model model, @RequestParam(required = false,defaultValue = "-1") int jobGroup) {
        Map<String, Object> map = new HashMap();
        map.put("ExecutorRouteStrategyEnum", JobConst.EXECUTOR_ROUTE_STRATEGY_ENUM);
        map.put("GlueTypeEnum", JobConst.GLUE_TYPE_ENUM);
        map.put("ExecutorBlockStrategyEnum", JobConst.EXECUTOR_BLOCK_STRATEGY_ENUM);
        List<XxlJobGroup> jobGroupList_all = this.xxlJobGroupDao.findAll();
        List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
        if (jobGroupList != null && jobGroupList.size() != 0) {
            map.put("JobGroupList", jobGroupList);
            map.put("jobGroup", jobGroup);
            return new ReturnT(map);
        } else {
            throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
        }
    }

    public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all) {
        List<XxlJobGroup> jobGroupList = new ArrayList();
        if (jobGroupList_all != null && jobGroupList_all.size() > 0) {
            XxlJobUser loginUser = TokenUtil.getClaim(request);
            if (loginUser.getRole() == 1) {
                jobGroupList = jobGroupList_all;
            } else {
                List<String> groupIdStrs = new ArrayList();
                if (loginUser.getPermission() != null && loginUser.getPermission().trim().length() > 0) {
                    groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
                }

                Iterator var5 = jobGroupList_all.iterator();

                while(var5.hasNext()) {
                    XxlJobGroup groupItem = (XxlJobGroup)var5.next();
                    if (((List)groupIdStrs).contains(String.valueOf(groupItem.getId()))) {
                        ((List)jobGroupList).add(groupItem);
                    }
                }
            }
        }

        return (List)jobGroupList;
    }

    public static void validPermission(HttpServletRequest request, int jobGroup) {
        XxlJobUser loginUser = TokenUtil.getClaim(request);
        if (!loginUser.validPermission(jobGroup)) {
            throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username=" + loginUser.getUsername() + "]");
        }
    }

    @RequestMapping({"/pageList"})
    @ResponseBody
    public ReturnT<Map<String, Object>> pageList(@RequestParam(required = false,defaultValue = "0") int start, @RequestParam(required = false,defaultValue = "10") int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        return new ReturnT(this.xxlJobService.pageList(start == 0 ? 0 : start * length, length, jobGroup, triggerStatus, jobDesc, executorHandler, author));
    }

    @RequestMapping({"/add"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> add(XxlJobInfoDto jobInfo) {
        return this.xxlJobService.add(jobInfo);
    }

    @RequestMapping({"/update"})
    @ResponseBody
    public ReturnT<String> update(XxlJobInfo jobInfo) {
        return this.xxlJobService.update(jobInfo);
    }

    @RequestMapping({"/remove"})
    @ResponseBody
    public ReturnT<String> remove(int id) {
        return this.xxlJobService.remove(id);
    }

    @RequestMapping({"/stop"})
    @ResponseBody
    public ReturnT<String> pause(int id) {
        return this.xxlJobService.stop(id);
    }

    @RequestMapping({"/start"})
    @ResponseBody
    public ReturnT<String> start(int id) {
        return this.xxlJobService.start(id);
    }

    @RequestMapping({"/trigger"})
    @ResponseBody
    public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, (String)null, executorParam, addressList);
        return ReturnT.SUCCESS;
    }
}
