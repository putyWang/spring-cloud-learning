package com.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.entity.ProjectEntity;
import com.learning.mapper.ProjectMapper;
import com.learning.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl
        extends ServiceImpl<ProjectMapper, ProjectEntity>
        implements ProjectService {
}
