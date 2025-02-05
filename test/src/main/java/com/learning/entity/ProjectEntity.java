package com.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 短信验证码token
 */
@Data
@TableName("project_0")
public class ProjectEntity {

    private Long id;

    private String name;

    private Long adminId;
}


