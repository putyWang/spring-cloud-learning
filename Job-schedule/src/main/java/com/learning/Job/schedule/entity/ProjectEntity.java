package com.learning.Job.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 短信验证码token
 */
@Data
@TableName("project")
public class ProjectEntity {

    private Long id;

    private String name;

    private Long adminId;
}


