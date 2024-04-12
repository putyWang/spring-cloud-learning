package com.learning.system.service;

import com.learning.core.model.RoleModel;
import com.learning.system.model.dto.RolePermissionDto;
import com.learning.system.model.entity.RolePermissionEntity;
import com.learning.web.service.BaseService;

import java.util.List;

public interface RolePermissionService
        extends BaseService<RolePermissionDto, RolePermissionEntity> {

    /**
     * 获取指定角色 id 列表对应的所有权限
     * @param roleIdList
     * @return
     */
    List<RoleModel> listByRoleIds(List<Long> roleIdList);
}
