package com.learning.job.schedule.dao;

import com.learning.job.schedule.core.model.XxlJobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface XxlJobRegistryDao {
    List<Integer> findDead(@Param("timeout") int timeout, @Param("nowTime") Date nowTime);

    int removeDead(@Param("ids") List<Integer> ids);

    List<XxlJobRegistry> findAll(@Param("timeout") int timeout, @Param("nowTime") Date nowTime);

    int registryUpdate(@Param("updateTime") Date updateTime, @Param("registryGroup") String registryGroup, @Param("registryKey") String registryKey, @Param("registryValue") String registryValue);

    int registrySave(@Param("registryGroup") String registryGroup, @Param("registryKey") String registryKey, @Param("registryValue") String registryValue, @Param("updateTime") Date updateTime);

    int registryDelete(@Param("registryGroup") String registGroup, @Param("registryKey") String registryKey, @Param("registryValue") String registryValue);
}
