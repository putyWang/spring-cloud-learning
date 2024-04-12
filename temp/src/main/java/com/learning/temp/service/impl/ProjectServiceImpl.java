package com.learning.temp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.temp.entity.ProjectEntity;
import com.learning.temp.mapper.ProjectMapper;
import com.learning.temp.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl
        extends ServiceImpl<ProjectMapper, ProjectEntity>
        implements ProjectService {
}
