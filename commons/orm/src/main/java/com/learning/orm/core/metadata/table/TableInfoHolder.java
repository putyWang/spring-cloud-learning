package com.learning.orm.core.metadata.table;

import cn.hutool.core.util.ObjectUtil;
import com.learning.core.utils.SpringContextHolder;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.relational.core.mapping.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/8 下午8:11
 */
public class TableInfoHolder {

    private static final Map<Class<?>, TableInfoMetadata> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    public static TableInfoMetadata obtainTableInfo(Class<?> entityType) {
        // 1 处理空类型
        if (ObjectUtil.isNull(entityType)) {
            return TableInfoMetadata.EMPTY_INFO;
        }
        // 2 从缓存中获取表信息
        return TABLE_INFO_CACHE.computeIfAbsent(entityType, TableInfoHolder::convert);
    }


    @SuppressWarnings("all")
    private static TableInfoMetadata convert(Class<?> entityType) {
        TableInfoMetadata tableInfoMetadata = new TableInfoMetadata();
        RelationalMappingContext mappingContext = SpringContextHolder.getBean(RelationalMappingContext.class);
        // 1 获取 entityType 对应元数据
        RelationalPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entityType);
        // 2 设置表名
        tableInfoMetadata.setName(persistentEntity.getTableName().getReference());
        // 3 通过 MappingContext 提取映射 id 元数据
        RelationalPersistentProperty idProperty = persistentEntity.getIdProperty();
        FieldMeta idInfo = new FieldMeta();
        idInfo.setName(idProperty.getName());
        idInfo.setColumn(idProperty.getColumnName().getReference());
        idInfo.setJavaType(idProperty.getType());
        tableInfoMetadata.setIdInfo(idInfo);
        // 4 设置其余字段信息
        persistentEntity.doWithProperties(
                (PropertyHandler<RelationalPersistentProperty>) property -> {
                    FieldMeta fieldMeta = new FieldMeta();
                    fieldMeta.setName(property.getName());
                    fieldMeta.setColumn(property.getColumnName().getReference());
                    fieldMeta.setJavaType(property.getType());
                    tableInfoMetadata.getFieldList().add(fieldMeta);
                }
        );

        return tableInfoMetadata;
    }
}
