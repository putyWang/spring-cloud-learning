package com.learning.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.web.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermissionEntity extends BaseEntity {

    /**
     * 父权限id
     */
    private Long roleId;

    /**
     * 权限名
     */
    private Long permissionId;
}
