package com.learning.system.service;

import com.learning.core.domain.model.UserContext;
import com.learning.system.model.dto.UserDto;
import com.learning.system.model.entity.UserEntity;
import com.learning.web.service.BaseService;

public interface UserService
        extends BaseService<UserDto, UserEntity> {

    /**
     * 用户注册
     * @param userDto
     */
    void registered(UserDto userDto);

    /**
     * 根据用户名获取用户信息
     * @param userName
     * @return
     */
    UserEntity getByUserName(String userName);
}
