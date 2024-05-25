package com.learning.job.utils;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.config.JobConfiguration;
import com.learning.job.model.Job;
import com.learning.job.model.XxlJobGroup;
import com.learning.job.model.XxlJobInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

@Log4j2
public class JobUtil {
    private static final String ADMIN_JOB = "ADMIN-JOB";
    private static final String ADD_JOB_INFO = "/jobinfo/v2/add";
    private static final String UPDATE_JOB_INFO = "/jobinfo/v2/update";
    private static final String LOAD_JOB_INFO = "/jobinfo/v2/loadById";
    private static final String REMOVE_JOB_INFO = "/jobinfo/v2/remove";
    private static final String STOP_JOB_INFO = "/jobinfo/v2/stop";
    private static final String START_JOB_INFO = "/jobinfo/v2/start";
    private static final String ADD_JOB_GROUP = "/jobgroup/v2/save";
    private static final String UPDATE_JOB_GROUP = "/jobgroup/v2/update";
    private static final String REMOVE_JOB_GROUP = "/jobgroup/v2/remove";
    private static final String REMOVE_LOAD_BY_ID = "/jobgroup/v2/loadById";
    private static final String COMPLETE_LOG = "/joblog/completeLog";

    public static String getJobHost() {
        String adminPath = JobConfiguration.adminPath;
        int index;
        if (StringUtils.isEmpty(adminPath)) {
            List<String> servicesList = DiscoveryUtil.getServicesList("ADMIN-JOB");
            index = (int)(Math.random() * (double)servicesList.size());
            adminPath = servicesList.get(index);
        } else {
            String[] split = adminPath.split(",");
            index = (int)(Math.random() * (double)split.length);
            adminPath = split[index];
        }

        return adminPath;
    }

    public static ReturnT<String> loadJobInfo(int id) {
        try {
            Map<String, String> m = new HashMap();
            m.put("id", id + "");
            String s = HttpUtil.postFromParam("http://" + getJobHost() + "/jobinfo/v2/loadById", m);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> deleteJobInfo(XxlJobInfo jobInfo) {
        try {
            String s = HttpUtil.sendHttpPost("http://" + getJobHost() + "/jobinfo/v2/delete", jobInfo);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var2) {
            Exception e = var2;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> addJobInfo(Job job) {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setJobGroup(job.getJobGroup());
        jobInfo.setJobCron(job.getJobCron());
        jobInfo.setJobDesc(job.getJobDesc());
        jobInfo.setAuthor(job.getApplicationName());
        jobInfo.setExecutorHandler(job.getExecutorHandler());
        jobInfo.setExecutorParam(job.getExecutorParam());
        jobInfo.setExecutorFailRetryCount(job.getExecutorFailRetryCount());
        jobInfo.setTriggerStatus(job.getTriggerStatus());
        jobInfo.setAllowRepeatAdd(job.getAllowRepeatAdd());
        jobInfo.setCronType(job.getCronType());

        try {
            String s = HttpUtil.sendHttpPost("http://" + getJobHost() + "/jobinfo/v2/add", jobInfo);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> updateJobInfo(XxlJobInfo jobInfo) {
        try {
            String s = HttpUtil.sendHttpPost("http://" + getJobHost() + "/jobinfo/v2/update", jobInfo);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var2) {
            Exception e = var2;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> removeJobInfo(String id) {
        try {
            Map<String, String> m = new HashMap();
            m.put("id", id);
            String s = HttpUtil.postFromParam("http://" + getJobHost() + "/jobinfo/v2/remove", m);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> stopJobInfo(String id) {
        try {
            Map<String, String> m = new HashMap();
            m.put("id", id);
            String s = HttpUtil.postFromParam("http://" + getJobHost() + "/jobinfo/v2/stop", m);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> startJobInfo(String id) {
        try {
            Map<String, String> m = new HashMap();
            m.put("id", id);
            String s = HttpUtil.postFromParam("http://" + getJobHost() + "/jobinfo/v2/start", m);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> addGroup(XxlJobGroup xxlJobGroup) {
        try {
            String s = HttpUtil.sendHttpPost("http://" + getJobHost() + "/jobgroup/v2/save", xxlJobGroup);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var2) {
            Exception e = var2;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> updateGroup(XxlJobGroup xxlJobGroup) {
        try {
            String s = HttpUtil.sendHttpPost("http://" + getJobHost() + "/jobgroup/v2/update", xxlJobGroup);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var2) {
            Exception e = var2;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> removeGroup(String id) {
        Map<String, String> m = new HashMap();
        m.put("id", id);

        try {
            String s = HttpUtil.postFromParam("http://" + getJobHost() + "/jobgroup/v2/remove", m);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<XxlJobGroup> getGroup(String id) {
        try {
            Map<String, String> m = new HashMap();
            m.put("id", id);
            String s = HttpUtil.postFromParam("http://" + getJobHost() + "/jobgroup/v2/loadById", m);
            ReturnT<XxlJobGroup> returnT = (ReturnT)JSON.parseObject(s, ReturnT.class);
            return returnT;
        } catch (Exception var4) {
            Exception e = var4;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static ReturnT<String> completeLog(long id, boolean isSuccess, String msg) {
        try {
            Map<String, Object> m = new HashMap();
            m.put("id", id + "");
            m.put("isSuccess", isSuccess);
            m.put("handleMsg", msg);
            String s = HttpUtil.sendHttpPost("http://" + getJobHost() + "/joblog/completeLog", m);
            return (ReturnT)JSON.parseObject(s, ReturnT.class);
        } catch (Exception var6) {
            Exception e = var6;
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}
