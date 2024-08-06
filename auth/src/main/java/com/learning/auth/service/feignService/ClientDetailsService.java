package com.learning.auth.service.feignService;

import com.learning.core.domain.constants.SecurityConstants;
import com.learning.core.domain.model.ApiResult;
import com.learning.core.domain.model.OauthClientDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调用后台服务接口
 * @author User
 */
@FeignClient(value = "system")
public interface ClientDetailsService {

    /**
     * 通过clientId 查询客户端信息
     * @param clientId 用户名
     * @param from 调用标志
     * @return R
     */
    @GetMapping("/client/getClientDetailsById/{clientId}")
    ApiResult<OauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId,
                                                       @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询全部客户端
     * @param from 调用标识
     * @return R
     */
    @GetMapping("/client/list")
    ApiResult<List<OauthClientDetails>> listClientDetails(@RequestHeader(SecurityConstants.FROM) String from);
}
