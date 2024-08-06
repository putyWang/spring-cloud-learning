package com.learning.security.service.impl;

import com.learning.security.model.LearningUser;
import com.learning.security.service.CustomUserDetailService;
import com.learning.security.service.feign.UserService;
import com.learning.core.domain.constants.SecurityConstants;
import com.learning.core.domain.model.UserContext;
import com.learning.core.utils.RetOps;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NameUserDetailServiceImpl extends CustomUserDetailService {

    @Resource
    private UserService userService;

    @Override
    public UserContext getUserInfoByName(String username) {
        UserContext userContext = new UserContext();
        userContext.setAccount(username);
        return RetOps.of(userService.info(userContext, SecurityConstants.FROM_IN)).getData().orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    @Override
    public UserDetails loadUserByUser(LearningUser user) {
        return loadUserByUsername(user.getUsername());
    }
}
