package com.learning.system.controller;

import com.learning.core.annotation.Module;
import com.learning.core.model.UserContext;
import com.learning.system.model.dto.LoginDto;
import com.learning.system.service.SysService;
import com.learning.system.service.UserService;
import io.prometheus.client.Summary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys")
@Tag(name = "系统调用接口", description = "系统调用接口")
public class SysController {

    @Resource
    private SysService sysService;

    @PostMapping("/login")
    @Operation(summary = "用户登陆", description = "用户登陆")
    public UserContext login(@RequestBody LoginDto loginDto) {
        return sysService.login(loginDto);
    }
}
