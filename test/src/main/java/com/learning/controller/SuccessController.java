package com.learning.controller;

import com.learning.entity.ProjectEntity;
import com.learning.fegin.Test1ProjectFeign;
import com.learning.service.ProjectService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@RequestMapping("/project")
@Log4j2
public class SuccessController {

    @Resource
    private ProjectService projectService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private Test1ProjectFeign test1ProjectFeign;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/{id}")
    public ProjectEntity query (@PathVariable Long id) {
        test1ProjectFeign.test();
        log.info("restTemplate 调用查询 id 为{}的项目", id);
        restTemplate.getForObject("http://192.168.3.51:8085/test1/project/" + id, ProjectEntity.class);
        log.info("本地正在查询 id 为{}的项目", id);
        return projectService.getById(id);
    }

    @PostMapping("/insert")
    public boolean insert (@RequestBody ProjectEntity projectEntity) {
        return projectService.save(projectEntity);
    }

    @PostMapping("/update")
    public boolean update (@RequestBody ProjectEntity projectEntity) {
        return projectService.updateById(projectEntity);
    }

    @PostMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return projectService.removeById(id);
    }

    @GetMapping("/test")
    public void test() {
        log.info("test");
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> log.info("test_{}", finalI));
        }
        log.info("test_complete");
    }
}
