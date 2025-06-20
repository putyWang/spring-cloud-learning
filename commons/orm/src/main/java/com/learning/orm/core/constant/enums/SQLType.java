package com.learning.orm.core.constant.enums;

import com.learning.orm.core.metadata.table.FieldMeta;
import com.learning.orm.core.metadata.table.TableInfoMetadata;

import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/8 下午9:24
 */
public enum SQLType {

    SELECT {
        @Override
        public String buildBaseSql(TableInfoMetadata tableInfoMetadata) {
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(
                    tableInfoMetadata.getFieldList()
                            .stream().map(FieldMeta::getColumn)
                            .collect(Collectors.joining(" "))
            );
            return sql.append(" FROM ").append(tableInfoMetadata.getName()).toString();
        }
    },
    UPDATE {
        @Override
        public String buildBaseSql(TableInfoMetadata tableInfoMetadata) {
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(
                    tableInfoMetadata.getFieldList()
                            .stream().map(FieldMeta::getColumn)
                            .collect(Collectors.joining(" "))
            );
            return sql.append(" FROM ").append(tableInfoMetadata.getName()).toString();
        }
    },
    INSERT {
        @Override
        public String buildBaseSql(TableInfoMetadata tableInfoMetadata) {
            StringBuilder sql = new StringBuilder("INSERT INTO ")
                    .append(tableInfoMetadata.getName());
            sql.append(
                    tableInfoMetadata.getFieldList()
                            .stream().map(FieldMeta::getColumn)
                            .collect(Collectors.joining(" "))
            );
            return sql.append(" FROM ").toString();
        }
    },
    DELETE {
        @Override
        public String buildBaseSql(TableInfoMetadata tableInfoMetadata) {
            return new StringBuilder("DELETE FROM ").append(tableInfoMetadata.getName()).toString();
        }
    };

    public String buildBaseSql(TableInfoMetadata tableInfoMetadata) {
        throw new RuntimeException("不支持生成该类型 sql");
    }
}
