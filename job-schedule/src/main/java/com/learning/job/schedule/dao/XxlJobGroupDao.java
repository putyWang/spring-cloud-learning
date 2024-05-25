package com.learning.job.schedule.dao;

import com.learning.job.schedule.core.model.XxlJobGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XxlJobGroupDao {
    List<XxlJobGroup> findAll();

    List<XxlJobGroup> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize);

    int findAllCount();

    List<XxlJobGroup> findByAddressType(@Param("addressType") Integer addressType);

    int save(XxlJobGroup xxlJobGroup);

    int update(XxlJobGroup xxlJobGroup);

    int findByappName(@Param("appName") String appName);

    Integer getIdByappName(@Param("appName") String appName);

    int notIdAppName(XxlJobGroup xxlJobGroup);

    int remove(@Param("id") int id);

    XxlJobGroup load(@Param("id") int id);
}
