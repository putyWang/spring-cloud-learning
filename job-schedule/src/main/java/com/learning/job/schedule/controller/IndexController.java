package com.learning.job.schedule.controller;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.schedule.annotation.PermissionLimit;
import com.learning.job.schedule.dto.LoginDto;
import com.learning.job.schedule.service.LoginService;
import com.learning.job.schedule.service.XxlJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private LoginService loginService;

    public IndexController() {
    }

    @RequestMapping({"/chartInfo"})
    @ResponseBody
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        ReturnT<Map<String, Object>> chartInfo = this.xxlJobService.chartInfo(startDate, endDate);
        return chartInfo;
    }

    @RequestMapping({"/groupCount"})
    @ResponseBody
    public ReturnT<Map<String, Object>> groupCount(Date startDate, Date endDate) {
        ReturnT<Map<String, Object>> chartInfo = this.xxlJobService.groupCount(startDate, endDate);
        return chartInfo;
    }

    @RequestMapping(
            value = {"login"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> login(@RequestBody LoginDto dto) {
        return this.loginService.login(dto);
    }

    @RequestMapping(
            value = {"logout"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    @PermissionLimit(
            limit = false
    )
    public ReturnT<String> logout() {
        return this.loginService.logout();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
