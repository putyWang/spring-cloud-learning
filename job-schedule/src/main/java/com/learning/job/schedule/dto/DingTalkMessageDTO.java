package com.learning.job.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DingTalkMessageDTO {
    private String robotId;
    private String content;
    private int msgType;
    private String linkUrl;
    private String picUrl;
    private String title;
    private boolean atAll;
    private List<String> atList;
    private int sendType = 1;
    private String startTime;
    private String endTime;
    private String frequency;
    private String unit;
}
