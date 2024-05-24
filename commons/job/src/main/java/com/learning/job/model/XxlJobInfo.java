package com.learning.job.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class XxlJobInfo {
    private int id;
    private int jobGroup;
    private String jobCron;
    private String jobDesc;
    private Date addTime;
    private Date updateTime;
    private String author;
    private String alarmEmail;
    private String executorRouteStrategy;
    private String executorHandler;
    private String executorParam;
    private String executorBlockStrategy;
    private int executorTimeout;
    private int executorFailRetryCount;
    private String glueType;
    private String glueSource;
    private String glueRemark;
    private Date glueUpdateTime;
    private String childJobId;
    private int triggerStatus;
    private long triggerLastTime;
    private long triggerNextTime;
    private Integer allowRepeatAdd;
    private Integer cronType = 1;
}
