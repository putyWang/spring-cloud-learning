package com.learning.Job.schedule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.Job.schedule.mapper.ProjectMapper;
import com.learning.Job.schedule.entity.ProjectEntity;
import com.learning.Job.schedule.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl
        extends ServiceImpl<ProjectMapper, ProjectEntity>
        implements ProjectService {
}
