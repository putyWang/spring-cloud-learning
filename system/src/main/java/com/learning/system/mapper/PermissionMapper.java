package com.learning.system.mapper;

import com.learning.system.model.entity.PermissionEntity;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import com.learning.web.mapper.RootMapper;

import java.util.List;

@Mapper
@CacheNamespace
public interface PermissionMapper extends RootMapper<PermissionEntity> {
    /**
     * 获取绑定指定用户菜单树
     * @return
     */
    List<PermissionEntity> getMenuTreeByRoleCode(Long roleId);
}
