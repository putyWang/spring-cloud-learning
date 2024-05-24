package com.learning.job.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Job {
    private int jobGroup;
    private String jobCron;
    private String jobDesc;
    private String applicationName;
    private String executorHandler;
    private String executorParam;
    private int executorFailRetryCount;
    private int triggerStatus;
    private Integer allowRepeatAdd;
    private Integer cronType = 0;

    public Job(String jobCron, String applicationName, String jobDesc, String executorHandler, String executorParam, Integer executorFailRetryCount) {
        this.jobCron = jobCron;
        this.applicationName = applicationName;
        this.jobDesc = jobDesc;
        this.executorHandler = executorHandler;
        this.executorParam = executorParam;
        if (null == executorFailRetryCount) {
            this.executorFailRetryCount = 0;
        } else {
            this.executorFailRetryCount = executorFailRetryCount;
        }

        if (null == this.allowRepeatAdd) {
            this.allowRepeatAdd = 1;
        }

        this.triggerStatus = 1;
    }
}
