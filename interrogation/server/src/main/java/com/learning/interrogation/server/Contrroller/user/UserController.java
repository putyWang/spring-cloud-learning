package com.learning.interrogation.server.Contrroller.user;

import com.learning.interrogation.domain.po.user.UserInfoPO;
import com.learning.interrogation.server.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/2 下午5:02
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


    private final UserInfoService userInfoService;

    @GetMapping("/update")
    public List<UserInfoPO> update(){
        return userInfoService.updateToken();
    }
}
