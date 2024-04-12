package com.learning.auth.service;

import com.learning.core.model.UserContext;
import com.learning.auth.entity.LearningUser;
import com.learning.auth.service.feignService.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class UserDetailService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SystemService systemService;

    /**
     * 获取用户信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserContext user = systemService.getUserByName(username);

        UserContext user = new UserContext();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName("1111");
        user.setAuthorities(new HashSet<>());
        return new LearningUser(username, user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getAuthorities().toArray(new String[0])),
                user.getRoles(), user.getMenus(), user.getRealName(), user.isSuperAdmin());
    }
}
