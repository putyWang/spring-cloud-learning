package com.learning.job.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;

import com.learning.core.utils.date.DateUtils;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.enums.ExecutorBlockStrategyEnum;
import com.learning.job.glue.GlueTypeEnum;
import com.learning.job.schedule.core.cron.CronExpression;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobInfoDto;
import com.learning.job.schedule.core.route.ExecutorRouteStrategyEnum;
import com.learning.job.schedule.core.utils.I18nUtil;
import com.learning.job.schedule.dao.*;
import com.learning.job.schedule.service.XxlJobService;
import com.learning.job.utils.DateUtil;
import com.learning.job.utils.DiscoveryUtil;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Log4j2
public class XxlJobServiceImpl implements XxlJobService {
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    private static final String TRIGGER_CHART_DATA_CACHE = "trigger_chart_data_cache";

    public XxlJobServiceImpl() {
    }

    public ReturnT<XxlJobInfo> loadById(int id) {
        XxlJobInfo xxlJobInfo = this.xxlJobInfoDao.loadById(id);
        return new ReturnT(xxlJobInfo);
    }

    public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        List<XxlJobInfo> list = this.xxlJobInfoDao.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        int list_count = this.xxlJobInfoDao.pageListCount(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        Map<String, Object> maps = new HashMap();
        maps.put("recordsTotal", list_count);
        maps.put("recordsFiltered", list_count);
        maps.put("data", list);
        return maps;
    }

    public ReturnT<String> add(XxlJobInfoDto jobInfo) {
        if (jobInfo.getAuthor() != null && jobInfo.getAuthor().trim().length() != 0) {
            if (0 == jobInfo.getJobGroup()) {
                Integer groupId = this.xxlJobGroupDao.getIdByappName(jobInfo.getAuthor());
                if (null != groupId) {
                    jobInfo.setJobGroup(groupId);
                } else {
                    XxlJobGroup xxlJobGroup = new XxlJobGroup();
                    xxlJobGroup.setAppName(jobInfo.getAuthor());
                    xxlJobGroup.setTitle(jobInfo.getJobDesc());
                    xxlJobGroup.setOrder(1);
                    xxlJobGroup.setAddressType(0);
                    this.xxlJobGroupDao.save(xxlJobGroup);
                    jobInfo.setJobGroup(xxlJobGroup.getId());
                }
            }

            if (! StringUtils.isEmpty(jobInfo.getJobCron()) && (1 == jobInfo.getCronType() || jobInfo.getCronType() == null)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss mm HH dd MM ? yyyy");

                try {
                    Date parse = simpleDateFormat.parse(jobInfo.getJobCron());
                    if (System.currentTimeMillis() > parse.getTime()) {
                        return new ReturnT(500, "添加失败，Cron时间小于系统当前时间！");
                    }
                } catch (ParseException e) {
                    log.error("corn 格式化失败: ss mm HH dd MM ? yyyy", e);
                }
            }

            if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
                return new ReturnT(500, I18nUtil.getString("jobinfo_field_cron_unvalid"));
            } else if (jobInfo.getJobDesc() != null && jobInfo.getJobDesc().trim().length() != 0) {
                if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), (ExecutorRouteStrategyEnum)null) == null) {
                    return new ReturnT(500, I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid"));
                } else if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), (ExecutorBlockStrategyEnum)null) == null) {
                    return new ReturnT(500, I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid"));
                } else if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
                    return new ReturnT(500, I18nUtil.getString("jobinfo_field_gluetype") + I18nUtil.getString("system_unvalid"));
                } else if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && (jobInfo.getExecutorHandler() == null || jobInfo.getExecutorHandler().trim().length() == 0)) {
                    return new ReturnT(500, I18nUtil.getString("system_please_input") + "JobHandler");
                } else {
                    if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
                        jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
                    }

                    ReturnT<String> childJobIdItem = this.ValidChildJobId(jobInfo);
                    if (childJobIdItem != null) {
                        return childJobIdItem;
                    } else {
                        if (StringUtils.isEmpty(jobInfo.getTriggerStatus())) {
                            jobInfo.setTriggerStatus(0);
                        }

                        if (null != jobInfo.getAllowRepeatAdd() && jobInfo.getAllowRepeatAdd() == 0) {
                            int sum = this.xxlJobInfoDao.getJobInfo(jobInfo);
                            if (sum > 0) {
                                return new ReturnT(500, "任务已存在！");
                            }
                        }

                        jobInfo.setAddTime(new Date());
                        jobInfo.setUpdateTime(new Date());
                        jobInfo.setGlueUpdateTime(new Date());
                        this.xxlJobInfoDao.save(jobInfo);
                        if (jobInfo.getId() < 1) {
                            return new ReturnT(500, I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail"));
                        } else {
                            String strJobInfo = JSON.toJSONString(jobInfo);
                            return new ReturnT(String.valueOf(strJobInfo));
                        }
                    }
                }
            } else {
                return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc"));
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author"));
        }
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException var3) {
            return false;
        }
    }

    public ReturnT<String> update(XxlJobInfo jobInfo) {
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT(500, I18nUtil.getString("jobinfo_field_cron_unvalid"));
        } else if (jobInfo.getJobDesc() != null && jobInfo.getJobDesc().trim().length() != 0) {
            if (jobInfo.getAuthor() != null && jobInfo.getAuthor().trim().length() != 0) {
                if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), (ExecutorRouteStrategyEnum)null) == null) {
                    return new ReturnT(500, I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid"));
                } else if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), (ExecutorBlockStrategyEnum)null) == null) {
                    return new ReturnT(500, I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid"));
                } else {
                    ReturnT<String> childJobIdItem = this.ValidChildJobId(jobInfo);
                    if (childJobIdItem != null) {
                        return childJobIdItem;
                    } else {
                        if (0 == jobInfo.getJobGroup()) {
                            Integer idByappName = this.xxlJobGroupDao.getIdByappName(jobInfo.getAuthor());
                            if (null == idByappName) {
                                return new ReturnT(500, I18nUtil.getString("jobinfo_field_jobgroup") + I18nUtil.getString("system_unvalid"));
                            }

                            jobInfo.setJobGroup(idByappName);
                        } else {
                            XxlJobGroup jobGroup = this.xxlJobGroupDao.load(jobInfo.getJobGroup());
                            if (jobGroup == null) {
                                return new ReturnT(500, I18nUtil.getString("jobinfo_field_jobgroup") + I18nUtil.getString("system_unvalid"));
                            }
                        }

                        XxlJobInfo exists_jobInfo = this.xxlJobInfoDao.loadById(jobInfo.getId());
                        if (exists_jobInfo == null) {
                            return new ReturnT(500, I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_not_found"));
                        } else {
                            exists_jobInfo.setTriggerStatus(jobInfo.getTriggerStatus());
                            long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
                            if (exists_jobInfo.getTriggerStatus() == 1 && !jobInfo.getJobCron().equals(exists_jobInfo.getJobCron())) {
                                try {
                                    Date nextValidTime = (new CronExpression(jobInfo.getJobCron())).getNextValidTimeAfter(new Date(System.currentTimeMillis() + 5000L));
                                    if (nextValidTime == null) {
                                        return new ReturnT(500, I18nUtil.getString("jobinfo_field_cron_never_fire"));
                                    }

                                    nextTriggerTime = nextValidTime.getTime();
                                } catch (ParseException var7) {
                                    ParseException e = var7;
                                    log.error(e.getMessage(), e);
                                    return new ReturnT(500, I18nUtil.getString("jobinfo_field_cron_unvalid") + " | " + e.getMessage());
                                }
                            }

                            if (StringUtils.isEmpty(jobInfo.getTriggerStatus())) {
                                jobInfo.setTriggerStatus(0);
                            }

                            exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
                            exists_jobInfo.setJobCron(jobInfo.getJobCron());
                            exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
                            exists_jobInfo.setAuthor(jobInfo.getAuthor());
                            exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
                            exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
                            exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
                            exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
                            exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
                            exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
                            exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
                            exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
                            exists_jobInfo.setTriggerNextTime(nextTriggerTime);
                            this.xxlJobInfoDao.update(exists_jobInfo);
                            return ReturnT.SUCCESS;
                        }
                    }
                }
            } else {
                return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author"));
            }
        } else {
            return new ReturnT(500, I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc"));
        }
    }

    private ReturnT<String> ValidChildJobId(XxlJobInfo jobInfo) {
        if (jobInfo.getChildJobId() != null && jobInfo.getChildJobId().trim().length() > 0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            String[] var3 = childJobIds;
            int var4 = childJobIds.length;
            int var5 = 0;

            while(true) {
                if (var5 >= var4) {
                    String temp = "";
                    String[] var9 = childJobIds;
                    var5 = childJobIds.length;

                    for(int var10 = 0; var10 < var5; ++var10) {
                        String item = var9[var10];
                        temp = temp + item + ",";
                    }

                    temp = temp.substring(0, temp.length() - 1);
                    jobInfo.setChildJobId(temp);
                    break;
                }

                String childJobIdItem = var3[var5];
                if (childJobIdItem == null || childJobIdItem.trim().length() <= 0 || !this.isNumeric(childJobIdItem)) {
                    return new ReturnT(500, MessageFormat.format(I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid"), childJobIdItem));
                }

                XxlJobInfo childJobInfo = this.xxlJobInfoDao.loadById(Integer.valueOf(childJobIdItem));
                if (childJobInfo == null) {
                    return new ReturnT(500, MessageFormat.format(I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found"), childJobIdItem));
                }

                ++var5;
            }
        }

        return null;
    }

    public ReturnT<String> remove(int id) {
        XxlJobInfo xxlJobInfo = this.xxlJobInfoDao.loadById(id);
        if (xxlJobInfo == null) {
            return ReturnT.SUCCESS;
        } else {
            this.xxlJobInfoDao.delete((long)id);
            this.xxlJobLogDao.delete(id);
            this.xxlJobLogGlueDao.deleteByJobId(id);
            return ReturnT.SUCCESS;
        }
    }

    public ReturnT<String> start(int id) {
        XxlJobInfo xxlJobInfo = this.xxlJobInfoDao.loadById(id);
        long nextTriggerTime = 0L;

        try {
            Date nextValidTime = (new CronExpression(xxlJobInfo.getJobCron())).getNextValidTimeAfter(new Date(System.currentTimeMillis() + 5000L));
            if (nextValidTime == null) {
                return new ReturnT(500, I18nUtil.getString("jobinfo_field_cron_never_fire"));
            }

            nextTriggerTime = nextValidTime.getTime();
        } catch (ParseException var6) {
            ParseException e = var6;
            log.error(e.getMessage(), e);
            return new ReturnT(500, I18nUtil.getString("jobinfo_field_cron_unvalid") + " | " + e.getMessage());
        }

        xxlJobInfo.setTriggerStatus(1);
        xxlJobInfo.setTriggerLastTime(0L);
        xxlJobInfo.setTriggerNextTime(nextTriggerTime);
        this.xxlJobInfoDao.update(xxlJobInfo);
        return ReturnT.SUCCESS;
    }

    public ReturnT<String> stop(int id) {
        XxlJobInfo xxlJobInfo = this.xxlJobInfoDao.loadById(id);
        xxlJobInfo.setTriggerStatus(0);
        xxlJobInfo.setTriggerLastTime(0L);
        xxlJobInfo.setTriggerNextTime(0L);
        this.xxlJobInfoDao.update(xxlJobInfo);
        return ReturnT.SUCCESS;
    }

    public Map<String, Object> dashboardInfo() {
        int jobInfoCount = this.xxlJobInfoDao.findAllCount();
        int jobLogCount = this.xxlJobLogDao.triggerCountByHandleCode(-1);
        int jobLogSuccessCount = this.xxlJobLogDao.triggerCountByHandleCode(200);
        Set<String> executerAddressSet = new HashSet();
        List<XxlJobGroup> groupList = this.xxlJobGroupDao.findAll();
        if (groupList != null && !groupList.isEmpty()) {
            Iterator var6 = groupList.iterator();

            while(var6.hasNext()) {
                XxlJobGroup group = (XxlJobGroup)var6.next();
                if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
                    executerAddressSet.addAll(group.getRegistryList());
                }
            }
        }

        int executorCount = executerAddressSet.size();
        Map<String, Object> dashboardMap = new HashMap();
        dashboardMap.put("jobInfoCount", jobInfoCount);
        dashboardMap.put("jobLogCount", jobLogCount);
        dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
        dashboardMap.put("executorCount", executorCount);
        return dashboardMap;
    }

    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        List<String> triggerDayList = new ArrayList();
        List<Integer> triggerDayCountRunningList = new ArrayList();
        List<Integer> triggerDayCountSucList = new ArrayList();
        List<Integer> triggerDayCountFailList = new ArrayList();
        int triggerCountRunningTotal = 0;
        int triggerCountSucTotal = 0;
        int triggerCountFailTotal = 0;
        List<Map<String, Object>> triggerCountMapAll = this.xxlJobLogDao.triggerCountByDay(startDate, endDate);
        int triggerDayCountFail;
        if (triggerCountMapAll != null && triggerCountMapAll.size() > 0) {
            for(Iterator var18 = triggerCountMapAll.iterator(); var18.hasNext(); triggerCountFailTotal += triggerDayCountFail) {
                Map<String, Object> item = (Map)var18.next();
                String day = String.valueOf(item.get("triggerDay"));
                int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
                int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
                int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
                triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;
                triggerDayList.add(day);
                triggerDayCountRunningList.add(triggerDayCountRunning);
                triggerDayCountSucList.add(triggerDayCountSuc);
                triggerDayCountFailList.add(triggerDayCountFail);
                triggerCountRunningTotal += triggerDayCountRunning;
                triggerCountSucTotal += triggerDayCountSuc;
            }
        } else {
            for(int i = 4; i > -1; --i) {
                triggerDayList.add(DateUtils.formatDate(DateUtil.addDays(new Date(), -i)));
                triggerDayCountRunningList.add(0);
                triggerDayCountSucList.add(0);
                triggerDayCountFailList.add(0);
            }
        }

        Map<String, Object> result = new HashMap();
        result.put("triggerDayList", triggerDayList);
        result.put("triggerDayCountRunningList", triggerDayCountRunningList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);
        result.put("triggerCountRunningTotal", triggerCountRunningTotal);
        result.put("triggerCountSucTotal", triggerCountSucTotal);
        result.put("triggerCountFailTotal", triggerCountFailTotal);
        return new ReturnT(result);
    }

    public ReturnT<Map<String, Object>> groupCount(Date startDate, Date endDate) {
        List<Map<String, Object>> groupCount = this.xxlJobLogDao.groupCount(startDate, endDate);
        List<String> nameList = new ArrayList();
        List<Integer> triggerDayCountCount = new ArrayList();
        List<Integer> triggerDayCountRunningList = new ArrayList();
        List<Integer> triggerDayCountSucList = new ArrayList();
        List<Integer> triggerDayCountFailList = new ArrayList();
        Map item;
        if (groupCount != null && groupCount.size() > 0) {
            Iterator var9 = groupCount.iterator();

            while(var9.hasNext()) {
                item = (Map)var9.next();
                String name = String.valueOf(item.get("name"));
                int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
                int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
                int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
                int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;
                nameList.add(name);
                triggerDayCountCount.add(triggerDayCount);
                triggerDayCountRunningList.add(triggerDayCountRunning);
                triggerDayCountSucList.add(triggerDayCountSuc);
                triggerDayCountFailList.add(triggerDayCountFail);
            }
        }

        Map<String, Object> result = new HashMap();
        result.put("nameList", nameList);
        result.put("triggerDayCountCount", triggerDayCountCount);
        result.put("triggerDayCountRunningList", triggerDayCountRunningList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);
        item = this.dashboardInfo();
        result.putAll(item);
        return new ReturnT(result);
    }

    @Transactional
    public ReturnT<String> registryByDiscovery(XxlJobGroup xxlJobGroup, String group) {
        List<ServiceInstance> serviceInstances = DiscoveryUtil.getServicesByDiscovery(xxlJobGroup.getAppName());
        StringBuilder stringBuilder = new StringBuilder();
        if (!CollectionUtils.isEmpty(serviceInstances)) {
            serviceInstances.forEach((val) -> {
                String registryValue = val.getHost() + ":" + val.getPort();
                Map<String, String> metadata = val.getMetadata();
                if (null != metadata) {
                    String contextPath = metadata.get("context-path");
                    if (!StringUtils.isEmpty(contextPath)) {
                        registryValue = registryValue + contextPath;
                    }
                }

                int ret = this.xxlJobRegistryDao.registryUpdate(new Date(), group, xxlJobGroup.getAppName(), registryValue);
                if (ret < 1) {
                    this.xxlJobRegistryDao.registrySave(group, xxlJobGroup.getAppName(), registryValue, new Date());
                }

                stringBuilder.append(registryValue).append(",");
            });
        }

        String addressList = stringBuilder.toString();
        if (addressList.endsWith(",")) {
            addressList = addressList.substring(0, addressList.length() - 1);
        }

        xxlJobGroup.setAddressList(addressList);
        this.xxlJobGroupDao.update(xxlJobGroup);
        return ReturnT.SUCCESS;
    }
}

