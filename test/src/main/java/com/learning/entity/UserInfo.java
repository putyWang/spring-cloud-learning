package com.learning.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/4/5 下午9:56
 */
@Data
@Accessors(chain = true)
public class UserInfo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名(登录账号)
     */
    private String username;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 用户昵称
     *
     * @ExcelProperty 的index 和 value 与数据库表结构顺序和字段名一致
     */
    private String nickname;


    /**
     * 盐
     */
    private String salt;

    /**
     * email
     */
    private String email;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 创建者ID
     */
    private Long createUserId;

    /**
     * 过期时间
     */
    private Date expiredDate;

    /**
     * 关联其他系统id，如his工号
     */
    private String foreignId;

    /**
     * 状态  0：禁用   1：正常
     * 0：锁住   1：正常
     */
    private Integer status;

    /**
     * 十分钟内连续登录错误次数，十分钟内连续5次登录失败则锁定账号
     */
    private Integer loginErrorCount;

    /**
     * 第一次登录错误时间
     */
    private Date firstErrorLoginTime;
}
