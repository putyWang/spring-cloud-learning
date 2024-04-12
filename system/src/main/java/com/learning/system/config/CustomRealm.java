package com.learning.system.config;

import com.learning.core.config.shiro.CommonRealm;
import com.learning.core.model.RoleModel;
import com.learning.core.model.UserContext;
import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.CommonBeanUtil;
import com.learning.core.utils.StringUtils;
import com.learning.system.model.entity.UserEntity;
import com.learning.system.service.RolePermissionService;
import com.learning.system.service.UserRoleService;
import com.learning.system.service.UserService;
import org.apache.shiro.authc.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomRealm extends CommonRealm {

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private RolePermissionService rolePermissionService;

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

        //2.获取登陆用户信息
        String username = token.getUsername();
        if (StringUtils.isBlank(username)) {
            //账户异常
            throw new AccountException("用户名不能为空");
        }

        UserEntity userEntity = userService.getByUserName(username);

        if (userEntity == null) {
            throw new AccountException(String.format("不存在账户为%s的用户", username));
        }

        // 3.验证用户密码
        String password = token.getPassword().toString();
        if (StringUtils.isEmpty(password) || ! password.equals(userEntity.getPassword())) {
            throw new AccountException("密码有误");
        }
        // 4.验证用户是否运行登陆
        if (userEntity.getStatus()!= 1) {
            //用户锁定
            throw new LockedAccountException();
        }
        // 5.生成用户信息
        UserContext userContext = CommonBeanUtil.copyAndFormat(UserContext.class, userEntity);
        userContext.setAccount(username);
        userContext.setUserId(userEntity.getId());
        // 6.获取角色信息
        List<RoleModel> roleModels = userRoleService.listRoleByUserId(userEntity.getId());

        if (CollectionUtils.isEmpty(roleModels)) {
            throw new AccountException(String.format("%s未设置角色，请联系管理员", username));
        }

        // 7.设置权限信息
        List<RoleModel> rolePermissionList = rolePermissionService.listByRoleIds(roleModels.stream()
                .map(RoleModel::getRoleId)
                .collect(Collectors.toList()));

        if (CollectionUtils.isEmpty(rolePermissionList)) {
            throw new AccountException(String.format("%s的角色未设置权限信息，请联系管理员", username));
        }

        Map<Long, List<String>> map = rolePermissionList.stream()
                .collect(Collectors.toMap(RoleModel::getRoleId, RoleModel::getPermissionList));

        roleModels.forEach(roleModel -> {
            roleModel.setPermissionList(map.get(roleModel.getRoleId()));
        });

        userContext.setRoles(roleModels);
        return new SimpleAuthenticationInfo(userContext, userEntity.getPassword(),userContext.getRealName());
    }
}
