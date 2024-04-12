package com.learning.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RootMapper<T> extends BaseMapper<T> {

    int insertBatch(@Param("list") List<T> list);

    int updateBatch(@Param("list") List<T> list);
}
