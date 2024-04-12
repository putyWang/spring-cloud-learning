package com.learning.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.web.model.entity.AdditionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class PermissionEntity extends AdditionEntity {

    /**
     * 父权限id
     */
    private Long pid;

    /**
     * 权限名
     */
    private String name;

    /**
     * 菜单权限url
     */
    private String url;

    /**
     * 后台权限验证值
     */
    private String permission;

    /**
     * 权限类型 0：菜单 1：导航按钮 2:普通按钮
     */
    private Integer permissionsType;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单排序
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Integer isEnabled;
}
