package com.learning.auth.config;

import com.learning.auth.service.feignService.SystemService;
import com.learning.core.config.shiro.CommonRealm;
import com.learning.core.model.UserContext;
import com.learning.core.utils.StringUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends CommonRealm {

    @Autowired
    private SystemService systemService;

    @Override
    public void setName(String name) {
        super.setName("customRealm");
    }

    /**
     * 认证匹配用户是否存在
     * @param authenticationToken 		shiro subject的认证信息
     * @return 							认证成功
     * @throws AuthenticationException 	认证失败
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.获取登录的token
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        //2.获取用户名
        String username = token.getUsername();
        if (StringUtils.isBlank(username)) {
            //账户异常
            throw new AccountException("用户名不能为空");
        }

        //3.数据库查询用户
        UserContext userEntity = this.systemService.getUserByName(username);

        if (userEntity == null) {
            throw new UnknownAccountException();
        }
        if (userEntity.getStatus()!=1) {
            //用户锁定
            throw new LockedAccountException();
        }

        return new SimpleAuthenticationInfo(userEntity,userEntity.getPassword(),this.getName());
    }

}