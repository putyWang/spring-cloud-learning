package com.learning.orm.config;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.orm.annotation.CompositeId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: UpdateBatchPatch
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class UpdateBatchPatch {

    private UpdateBatchPatch(){}

    public static final Map<String, String> TABLE_COMPOSITE_ID = new ConcurrentHashMap<>();

    static {
        for (Class<?> cla : ClassUtil.scanPackageByAnnotation("com.learning", CompositeId.class)) {
            CompositeId compositeIdAnnotation = cla.getAnnotation(CompositeId.class);
            String compositeIdValue = compositeIdAnnotation.value();
            TableName tableNameAnnotation = cla.getAnnotation(TableName.class);
            String tableNameValue = tableNameAnnotation.value();
            TABLE_COMPOSITE_ID.put(tableNameValue, compositeIdValue);
        }
    }
}
