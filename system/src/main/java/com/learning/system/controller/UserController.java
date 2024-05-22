package com.learning.system.controller;


import com.learning.core.annotation.Module;
import com.learning.system.model.dto.UserDto;
import com.learning.system.model.entity.UserEntity;
import com.learning.system.service.UserService;
import com.learning.web.controller.BaseController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Module("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Override
    public UserService getService() {
        return userService;
    }

    @Override
    public UserDto getDto() {
        return new UserDto();
    }

    @Override
    public UserEntity getEntity() {
        return new UserEntity();
    }
}
