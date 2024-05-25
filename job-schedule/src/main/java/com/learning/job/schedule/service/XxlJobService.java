package com.learning.job.schedule.service;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.core.model.XxlJobGroup;
import com.learning.job.schedule.core.model.XxlJobInfo;
import com.learning.job.schedule.core.model.XxlJobInfoDto;

import java.util.Date;
import java.util.Map;

public interface XxlJobService {
    ReturnT<XxlJobInfo> loadById(int id);

    Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    ReturnT<String> add(XxlJobInfoDto jobInfo);

    ReturnT<String> update(XxlJobInfo jobInfo);

    ReturnT<String> remove(int id);

    ReturnT<String> start(int id);

    ReturnT<String> stop(int id);

    Map<String, Object> dashboardInfo();

    ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);

    ReturnT<Map<String, Object>> groupCount(Date startDate, Date endDate);

    ReturnT<String> registryByDiscovery(XxlJobGroup xxlJobGroup, String group);
}

