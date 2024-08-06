package com.learning.system.mapper;

import com.learning.core.domain.model.RoleModel;
import com.learning.system.model.entity.RolePermissionEntity;
import com.learning.web.mapper.RootMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@CacheNamespace
public interface RolePermissionMapper extends RootMapper<RolePermissionEntity> {

    /**
     * 获取指定角色 id 列表对应的所有权限
     * @param roleIdList
     * @return
     */
    List<RoleModel> selectListByRoleIds(List<Long> roleIdList);
}
