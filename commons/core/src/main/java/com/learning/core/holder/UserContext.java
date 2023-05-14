
package com.learning.core.holder;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

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
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

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
    private List<String> roles;

    /**
     * 用户权限集合
     */
    private Set<String> authorities;


    private List<String> menus;

    /**
     * 机构代码
     */
    private String orgCode;

    /**
     * 系统代码
     */
    private String sysCode;

    /**
     * 本地信息对象
     */
    private Locale locale;
    private Integer type;
    private String tenantCode;
    private HashMap<Long, List<String>> desensitiseMap = new HashMap();
    private Map<String, Object> params = new HashMap();
}
