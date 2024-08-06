package com.learning.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.core.domain.annotation.Unique;
import com.learning.web.model.entity.AdditionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class RoleEntity extends AdditionEntity {

    /**
     * 角色名称
     */
    @Unique
    private String roleName;

    /**
     * 角色编码
     */
    @Unique
    private String roleCode;

    /**
     * 职能级别，管理员为1，用户为2
     */
    private Integer roleLevel;

    /**
     * 角色首页路由
     */
    private String indexUrl;

    /**
     * 角色描述
     */
    private String roleDesc;
}
