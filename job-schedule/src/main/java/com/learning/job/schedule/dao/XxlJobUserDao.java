package com.learning.job.schedule.dao;

import com.learning.job.schedule.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XxlJobUserDao {
    List<XxlJobUser> pageList(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("username") String username, @Param("role") int role);

    int pageListCount(@Param("offset") int offset, @Param("pagesize") int pagesize, @Param("username") String username, @Param("role") int role);

    XxlJobUser loadByUserName(@Param("username") String username);

    int save(XxlJobUser xxlJobUser);

    int update(XxlJobUser xxlJobUser);

    int delete(@Param("id") int id);
}