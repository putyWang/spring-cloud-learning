package com.learning.system.mapper;

import com.learning.system.model.entity.RoleEntity;
import com.learning.web.mapper.RootMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@CacheNamespace
public interface RoleMapper extends RootMapper<RoleEntity> {
}
