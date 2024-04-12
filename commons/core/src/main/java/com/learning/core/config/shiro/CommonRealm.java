package com.learning.core.config.shiro;

import com.learning.core.model.RoleModel;
import com.learning.core.model.UserContext;
import com.learning.core.utils.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

/**
 * 公共授权realm域
 */
public class CommonRealm extends AuthorizingRealm {

    @Override
    public void setName(String name) {
        super.setName("RealmCommon");
    }

    /**
     * 只重写授权方法
     *
     * @param principalCollection 身份信息集合
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取认证的用户数据 | devtools冲突导致无法强转,需更改类加载器:resources/META-INF/spring-devtools.properties
        UserContext user = (UserContext) principalCollection.getPrimaryPrincipal();
        //2.构造认证数据
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        List<RoleModel> roles = user.getRoles();
        if (CollectionUtils.isEmpty(roles)) {
            // 3.用户没有角色
            throw new AuthorizationException();
        }

        for (RoleModel role : roles) {
            // 4.添加角色信息
            info.addRole(role.getCode());
            // 5.添加权限信息
            List<String> permissions = role.getPermissionList();
            if (!CollectionUtils.isEmpty(permissions)) {
                permissions.forEach(permission -> info.addStringPermission(permission));
            }
        }

        return info;
    }

    /**
     * 认证方法在登录模块中补全
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        return null;
    }
}