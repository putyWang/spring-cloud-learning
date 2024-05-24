package com.learning.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @ClassName: RootMapper
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public interface RootMapper<T> extends BaseMapper<T> {

    /**
     * 批量新增部分字段
     * 实际逻辑在 InsertBatchSomeColumn 类中实现
     *
     * @param entityList
     * @return
     */
    int insertBatchSomeColumn(@Param("list") Collection<T> entityList);

    /**
     * 批量更新部分字段
     * 实际逻辑在 UpdateBatchSomeColumn 类中实现
     *
     * @param entityList
     * @return
     */
    int updateBatchSomeColumn(@Param("list") Collection<T> entityList);
}
