//package com.learning.orm.model;
//
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//
//import java.util.Map;
//
///**
// * @ClassName: UpdateModel
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//public class UpdateModel extends BaseModel {
//    public UpdateModel(String dataBaseName, String tableName, Map<String, Object> fieldMap) {
//        this.dataBaseName = dataBaseName;
//        this.tableName = tableName;
//        this.fieldMap = fieldMap;
//    }
//
//    public int update(Wrapper query) {
//        return MAPPER.update(query, this);
//    }
//}
