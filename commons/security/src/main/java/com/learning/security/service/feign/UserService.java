package com.learning.security.service.feign;

import com.learning.core.constants.SecurityConstants;
import com.learning.core.model.ApiResult;
import com.learning.core.model.UserContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient
public interface UserService {

    /**
     * 通过用户名查询用户、角色信息
     * @param user 用户查询对象
     * @return R
     */
    @GetMapping("/user/info/query")
    ApiResult<UserContext> info(@SpringQueryMap UserContext user, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 锁定用户
     * @param username 用户名
     * @return
     */
    @PutMapping("/user/lock/{username}")
    ApiResult<Boolean> lockUser(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM) String from);
}
