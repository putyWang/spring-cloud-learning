package com.learning.auth.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 自定义用户信息对象
 */
public class LearningUser extends User {

    public LearningUser(String username, String password, Collection<? extends GrantedAuthority> authorities, List<String> roles, List<String> menus, String realName, boolean isSuperAdmin){
        super(username, password, authorities);
        this.roles = roles;
        this.menus = menus;
        this.realName = realName;
        this.isSuperAdmin = isSuperAdmin;
    }

    public LearningUser(String username, String password, boolean enabled, boolean accountNonExpired,
                  boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, List<String> roles, List<String> menus, String realName, boolean isSuperAdmin){
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.roles = roles;
        this.menus = menus;
        this.realName = realName;
        this.isSuperAdmin = isSuperAdmin;
    }

    /**
     * 用户角色集合
     */
    private List<String> roles;


    /**
     * 用户菜单集合
     */
    private List<String> menus;

    /**
     * 真实名字
     */
    private String realName;

    /**
     * 是否为超级管理员
     */
    private boolean isSuperAdmin;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getMenus() {
        return menus;
    }

    public void setMenus(List<String> menus) {
        this.menus = menus;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LearningUser myUser = (LearningUser) o;
        return isSuperAdmin == myUser.isSuperAdmin &&
                Objects.equals(roles, myUser.roles) &&
                Objects.equals(menus, myUser.menus) &&
                Objects.equals(realName, myUser.realName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roles, menus, realName, isSuperAdmin);
    }
}
