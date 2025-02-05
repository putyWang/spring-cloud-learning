package com.learning.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.test1.entity.ProjectEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<ProjectEntity> {

}
