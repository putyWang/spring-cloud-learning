package com.learning.test1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.test1.entity.ProjectEntity;
import com.learning.test1.mapper.ProjectMapper;
import com.learning.test1.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl
        extends ServiceImpl<ProjectMapper, ProjectEntity>
        implements ProjectService {
}
