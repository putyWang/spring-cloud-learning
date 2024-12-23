package com.learning.temp.controller;

import com.learning.temp.entity.ProjectEntity;
import com.learning.temp.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/project")
public class SuccessController {

    @Resource
    private ProjectService projectService;

    @GetMapping("/{id}")
    public ProjectEntity query (@PathVariable Long id) {
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
}
