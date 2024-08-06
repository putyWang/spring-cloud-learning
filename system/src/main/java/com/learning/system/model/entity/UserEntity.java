package com.learning.system.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.core.domain.annotation.Unique;
import com.learning.web.model.entity.AdditionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends AdditionEntity {

    /**
     * 用户名
     */
    @Unique
    private String username;

    /**
     * 用户昵称
     */
    @Unique
    private String nickName;

    /**
     * 真实姓名
     */
    @Unique
    private String realName;

    /**
     * 密码
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String password;

    /**
     * 性别
     * 1为男
     * 2为女
     */
    private Integer sex;

    /**
     * 电话号码
     */
    @Unique
    private String phone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 表示用户状态
     * 1为正常
     * 0为以禁用
     */
    private Integer status;
}
