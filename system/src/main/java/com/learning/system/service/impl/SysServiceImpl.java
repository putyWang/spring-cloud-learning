package com.learning.system.service.impl;

import com.learning.core.model.UserContext;
import com.learning.system.model.dto.LoginDto;
import com.learning.system.service.SysService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

@Service
public class SysServiceImpl implements SysService {

    @Override
    public UserContext login(LoginDto loginDto) {
        // 1.shiro 进行登陆
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken(loginDto.getUsername(), loginDto.getPassword()));
        return (UserContext)subject.getPrincipal();
    }
}
