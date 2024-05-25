package com.learning.job.schedule.controller;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.enums.ExecutorBlockStrategyEnum;
import com.learning.job.glue.GlueTypeEnum;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.core.exception.XxlJobException;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobInfoDto;
import com.learning.job.schedule.core.model.XxlJobUser;
import com.learning.job.schedule.core.route.ExecutorRouteStrategyEnum;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/jobinfo/v2"})
public class JobInfoV2Controller {
    private static final Logger log = LoggerFactory.getLogger(JobInfoV2Controller.class);
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobService xxlJobService;

    public JobInfoV2Controller() {
    }

    @RequestMapping({"/loadById"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<XxlJobInfo> loadById(int id) {
        return this.xxlJobService.loadById(id);
    }

    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false,defaultValue = "-1") int jobGroup) {
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());
        List<XxlJobGroup> jobGroupList_all = this.xxlJobGroupDao.findAll();
        List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
        if (jobGroupList != null && jobGroupList.size() != 0) {
            model.addAttribute("JobGroupList", jobGroupList);
            model.addAttribute("jobGroup", jobGroup);
            return "jobinfo/jobinfo.index";
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
    @PermissionLimit(
            limit = false
    )
    public Map<String, Object> pageList(@RequestParam(required = false,defaultValue = "0") int start, @RequestParam(required = false,defaultValue = "10") int length, @RequestParam int jobGroup, @RequestParam int triggerStatus, @RequestParam String jobDesc, @RequestParam String executorHandler, @RequestParam String author) {
        return this.xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }

    @RequestMapping({"/add"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> add(@RequestBody XxlJobInfoDto jobInfo) {
        log.info("jobinfoAdd: " + JSON.toJSONString(jobInfo));
        jobInfo.setGlueType("BEAN");
        if (StringUtils.isEmpty(jobInfo.getExecutorBlockStrategy())) {
            jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        }

        jobInfo.setExecutorTimeout(0);
        jobInfo.setAlarmEmail("");
        jobInfo.setExecutorRouteStrategy("FIRST");
        jobInfo.setAddTime(new Date());
        jobInfo.setUpdateTime(new Date());
        return this.xxlJobService.add(jobInfo);
    }

    @RequestMapping({"/update"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> update(@RequestBody XxlJobInfo jobInfo) {
        return this.xxlJobService.update(jobInfo);
    }

    @RequestMapping({"/remove"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> remove(@RequestParam int id) {
        return this.xxlJobService.remove(id);
    }

    @RequestMapping({"/stop"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> pause(@RequestParam int id) {
        return this.xxlJobService.stop(id);
    }

    @RequestMapping({"/start"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> start(@RequestParam int id) {
        return this.xxlJobService.start(id);
    }

    @RequestMapping({"/trigger"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> triggerJob(@RequestParam int id, @RequestParam String executorParam, String addressList) {
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, (String)null, executorParam, addressList);
        return ReturnT.SUCCESS;
    }
}
