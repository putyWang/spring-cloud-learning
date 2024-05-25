package com.learning.job.schedule.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class XxlJobLogGlue {
    private int id;
    private int jobId;
    private String glueType;
    private String glueSource;
    private String glueRemark;
    private Date addTime;
    private Date updateTime;
}
