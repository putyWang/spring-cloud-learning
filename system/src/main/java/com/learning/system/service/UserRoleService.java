package com.learning.system.service;

import com.learning.core.model.RoleModel;
import com.learning.system.model.dto.UserRoleDto;
import com.learning.system.model.entity.UserRoleEntity;
import com.learning.web.service.BaseService;

import java.util.List;

public interface UserRoleService extends BaseService<UserRoleDto, UserRoleEntity> {

    /**
     * 通过用户id获取角色
     * @param userId 用户id
     * @return
     */
    List<RoleModel> listRoleByUserId(Long userId);

    /**
     * 为用户绑定角色
     * @param userRole 用户角色绑定对象
     * @return
     */
    void bindRole(UserRoleEntity userRole);
}
