package com.learning.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: wangwei
 * @createDate: 2025/2/5 下午10:27
 * @version: 1.0
 */
@FeignClient(name = "test1", contextId = "test1", url = "http://192.168.3.51:8085/test1", path = "/project")
public interface Test1ProjectFeign {
    @GetMapping("/test")
    void test();
}
