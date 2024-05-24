package com.learning.orm.model;

import java.util.Map;

/**
 * @ClassName: InsertModel
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class InsertModel extends BaseModel {
    public InsertModel(String dataBaseName, String tableName, Map<String, Object> fieldMap, String keys) {
        this.dataBaseName = dataBaseName;
        this.tableName = tableName;
        this.fieldMap = fieldMap;
        this.keys = keys;
    }

    public int insert() {
        return MAPPER.insert(this);
    }
}

