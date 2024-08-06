package com.learning.system.mapper;

import com.learning.core.domain.model.RoleModel;
import com.learning.system.model.entity.UserRoleEntity;
import com.learning.web.mapper.RootMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@CacheNamespace
public interface UserRoleMapper extends RootMapper<UserRoleEntity> {

    List<RoleModel> selectRoleByUserId(Long userId);
}
