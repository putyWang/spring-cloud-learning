package com.learning.job.biz.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class TriggerParam implements Serializable {
    private static final long serialVersionUID = 42L;
    private int jobId;
    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;
    private long logId;
    private long logDateTim;
    private String glueType;
    private String glueSource;
    private long glueUpdateTime;
    private int broadcastIndex;
    private int broadcastTotal;
}
