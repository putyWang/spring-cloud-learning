package com.learning.auth.service.feignService;

import com.learning.core.holder.UserContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 调用后台服务接口
 */
@FeignClient(value = "system")
public interface SystemService {

    @RequestMapping(value = "/user/{userName}",method = RequestMethod.GET)
    UserContext getUserByName(@PathVariable("userName") String userName);
}
