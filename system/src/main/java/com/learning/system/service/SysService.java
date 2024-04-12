package com.learning.system.service;

import com.learning.core.model.UserContext;
import com.learning.system.model.dto.LoginDto;

/**
 * 系统相关服务
 */
public interface SysService {

    /**
     * 用户登陆
     * @param loginDto 登陆dto
     * @return
     */
    UserContext login(LoginDto loginDto);
}
