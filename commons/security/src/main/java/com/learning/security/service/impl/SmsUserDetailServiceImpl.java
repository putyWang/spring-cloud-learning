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
public class SmsUserDetailServiceImpl extends CustomUserDetailService {

    @Resource
    private UserService userService;

    /**
     * 排序值 默认取最大的
     * @return 排序值
     */
    public int getOrder() {
        return 0;
    }

    @Override
    public UserContext getUserInfoByName(String phone) {
        UserContext userContext = new UserContext();
        userContext.setPhone(phone);
        return RetOps.of(userService.info(userContext, SecurityConstants.FROM_IN)).getData().orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    @Override
    public UserDetails loadUserByUser(LearningUser user) {
        return loadUserByUsername(user.getPhone());
    }

    /**
     * 是否支持此客户端校验
     * @param clientId 目标客户端
     * @return true/false
     */
    @Override
    public boolean support(String clientId, String grantType) {
        return SecurityConstants.MOBILE.equals(grantType);
    }
}
