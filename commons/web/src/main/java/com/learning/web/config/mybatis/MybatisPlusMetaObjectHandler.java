package com.learning.web.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.learning.core.domain.model.UserContext;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 自动补充插入或更新时的值
 *
 * @author felix
 */
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增数据处理
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = getUserId();

        Object createBy = this.getFieldValByName("createBy", metaObject);
        if (createBy == null) {
            this.setFieldValByName("createBy", userId, metaObject);
        }

        this.setFieldValByName("createTime", new Date(), metaObject);

        Object updateBy = this.getFieldValByName("updateBy", metaObject);
        if (updateBy == null) {
            this.setFieldValByName("updateBy", userId, metaObject);
        }

        this.setFieldValByName("updateTime", new Date(), metaObject);


        this.setFieldValByName("version", 1, metaObject);
    }

    /**
     * 更新字段处理
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = getUserId();
        this.setFieldValByName("updateBy", userId, metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("version", this.getFieldValByName("version", metaObject), metaObject);
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public Long getUserId() {
        return 1L;
    }
}
