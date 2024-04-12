package com.learning.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.learning.system.model.dto.PermissionDto;
import com.learning.system.model.dto.RolePermissionDto;
import com.learning.system.model.entity.PermissionEntity;
import com.learning.web.model.param.PageParam;
import com.learning.web.service.BaseService;

import java.util.List;
import java.util.Map;

public interface PermissionService
        extends BaseService<PermissionDto, PermissionEntity> {

    /**
     * 分页查询权限树
     * @param page 分页查询参数
     * @return
     */
    IPage<PermissionDto> pageTree(PageParam page);

    /**
     * 获取菜单树
     * @return
     */
    List<PermissionDto> getMenuTree();

    /**
     * 获取指定角色相关权限树
     * @param roleId 角色 id
     */
    Map<String,List<PermissionDto>> getPermissionTree(Long roleId);

    /**
     * 批量绑定用户权限
     * @param rolePermission 用户权限对象
     */
    void insertPermissionRoleBatch(RolePermissionDto rolePermission);


    /**
     * 根据permissionType 获取所有父权限
     * @return
     */
    List<PermissionDto> listByPermissionsType(Integer permissionType);
}
