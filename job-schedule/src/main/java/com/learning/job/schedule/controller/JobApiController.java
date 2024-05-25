package com.learning.job.schedule.controller;

import com.learning.job.biz.AdminBiz;
import com.learning.job.biz.model.HandleCallbackParam;
import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class JobApiController {
    @Resource
    private AdminBiz adminBiz;

    public JobApiController() {
    }

    @PostMapping({"/api"})
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> api(@RequestBody List<HandleCallbackParam> callbackParamList) {
        return this.adminBiz.callback(callbackParamList);
    }
}
