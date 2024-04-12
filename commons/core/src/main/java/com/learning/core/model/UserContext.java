package com.learning.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 用户信息类
 */
@Data
public class UserContext implements Serializable {
    private String uuid;

    /**
     * 用户id
     */
    private Long userId = 0L;

    /**
     * 账户名
     */
    private String account;

    /**
     * 真实名字
     */
    private String realName;

    /**
     * 是否为超级管理员
     */
    private boolean isSuperAdmin;

    /**
     * 用户角色集合
     */
    private List<RoleModel> roles;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 系统代码
     */
    private String sysCode;

    /**
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 脱敏配置
     */
    private HashMap<Long, List<String>> desensitiseMap = new HashMap();
}
