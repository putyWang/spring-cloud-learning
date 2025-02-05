package com.learning.temp.controller;

import com.learning.temp.entity.ProjectEntity;
import com.learning.temp.service.ProjectService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/project")
@Log4j2
public class SuccessController {

    @Resource
    private ProjectService projectService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @GetMapping("/{id}")
    public ProjectEntity query (@PathVariable Long id) {
        log.info("正在查询 id 为{}的项目", id);
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

    @GetMapping("test")
    public void test() {
        log.info("test");
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> log.info("test_{}", finalI));
        }
        log.info("test_complete");
    }
}
