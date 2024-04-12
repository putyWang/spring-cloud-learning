package com.learning.system.mapper;

import com.learning.system.model.dto.UserDto;
import com.learning.system.model.entity.UserEntity;
import com.learning.web.mapper.RootMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@CacheNamespace
public interface UserMapper
        extends RootMapper<UserEntity> {

    UserDto selectByUsername(String usename);
}
