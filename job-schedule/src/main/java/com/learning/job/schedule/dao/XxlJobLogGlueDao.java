package com.learning.job.schedule.dao;

import com.learning.job.schedule.core.model.XxlJobLogGlue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XxlJobLogGlueDao {
    int save(XxlJobLogGlue xxlJobLogGlue);

    List<XxlJobLogGlue> findByJobId(@Param("jobId") int jobId);

    int removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

    int deleteByJobId(@Param("jobId") int jobId);
}
