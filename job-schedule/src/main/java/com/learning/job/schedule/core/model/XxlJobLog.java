package com.learning.job.schedule.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class XxlJobLog {
    private long id;
    private int jobGroup;
    private String appName;
    private int jobId;
    private String jobDesc;
    private String executorAddress;
    private String executorHandler;
    private String executorParam;
    private String executorShardingParam;
    private int executorFailRetryCount;
    private Date triggerTime;
    private int triggerCode;
    private String triggerMsg;
    private Date handleTime;
    private int handleCode;
    private String handleMsg;
    private int alarmStatus;
}

