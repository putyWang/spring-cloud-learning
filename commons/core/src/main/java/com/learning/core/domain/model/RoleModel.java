package com.learning.core.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class RoleModel {

    /**
     * 角色 id
     */
    private Long roleId;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 接口权限列表
     */
    private List<String> permissionList;
}
