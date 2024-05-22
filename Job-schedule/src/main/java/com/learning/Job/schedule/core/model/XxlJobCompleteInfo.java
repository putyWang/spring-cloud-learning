package com.learning.Job.schedule.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class XxlJobCompleteInfo implements Serializable {
    private long id;
    private boolean isSuccess;
    private String handleMsg;
}
