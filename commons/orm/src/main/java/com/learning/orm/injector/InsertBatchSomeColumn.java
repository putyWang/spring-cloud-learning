//package com.learning.orm.injector;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.core.enums.SqlMethod;
//import com.baomidou.mybatisplus.core.injector.AbstractMethod;
//import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
//import com.baomidou.mybatisplus.core.metadata.TableInfo;
//import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
//import com.learning.core.utils.StringUtil;
//import com.learning.orm.consts.DynamicTableConst;
//import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
//import org.apache.ibatis.executor.keygen.KeyGenerator;
//import org.apache.ibatis.executor.keygen.NoKeyGenerator;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlSource;
//
//import java.lang.reflect.Field;
//import java.util.Iterator;
//import java.util.List;
//import java.util.function.Predicate;
//
///**
// * @ClassName: YhInsertBatchSomeColumn
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//public class InsertBatchSomeColumn extends AbstractMethod {
//
//    private transient Predicate<TableFieldInfo> predicate;
//
//    protected InsertBatchSomeColumn(String methodName) {
//        super(methodName);
//    }
//
//    @Override
//    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
//        KeyGenerator keyGenerator = new NoKeyGenerator();
//        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
//        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
//        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(false, "", false) + this.filterTableFieldInfo(fieldList, this.predicate, TableFieldInfo::getInsertSqlColumn, "");
//        String zero = StringUtil.isNotBlank(insertSqlColumn) ? insertSqlColumn.substring(0, insertSqlColumn.length() - 1) : "id";
//        String columnScript = "(" + zero + ")";
//        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(false, "et.", false) + this.filterTableFieldInfo(fieldList, this.predicate, i -> {
//            String el = this.handleClassField(modelClass, i.getColumn());
//            return StringUtil.isBlank(el) ? i.getInsertSqlProperty("et.") : SqlScriptUtils.safeParam("et." + el) + ",";
//        }, "");
//        String zero1 = StringUtil.isNotBlank(insertSqlColumn) ? insertSqlProperty.substring(0, insertSqlProperty.length() - 1) : "id";
//        insertSqlProperty = "(" + zero1 + ")";
//        String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", (String)null, "et", ",");
//        String keyProperty = null;
//        String keyColumn = null;
//        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
//            if (tableInfo.getIdType() == IdType.AUTO) {
//                keyGenerator = new Jdbc3KeyGenerator();
//                keyProperty = tableInfo.getKeyProperty();
//                keyColumn = tableInfo.getKeyColumn();
//            } else if (null != tableInfo.getKeySequence()) {
//                keyGenerator = TableInfoHelper.genKeyGenerator(this.getMethod(sqlMethod), tableInfo, this.builderAssistant);
//                keyProperty = tableInfo.getKeyProperty();
//                keyColumn = tableInfo.getKeyColumn();
//            }
//        }
//
//        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
//        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
//        return this.addInsertMappedStatement(mapperClass, modelClass, this.getMethod(sqlMethod), sqlSource, (KeyGenerator)keyGenerator, keyProperty, keyColumn);
//    }
//
//    public String getMethod(SqlMethod sqlMethod) {
//        return "insertBatchSomeColumn";
//    }
//
//    private String handleClassField(Class<?> modelClass, String fieldName) {
//        List<Field> allField = DynamicTableUtil.getAllField(modelClass);
//        Iterator var4 = allField.iterator();
//
//        Field declaredField;
//        String name;
//        do {
//            if (!var4.hasNext()) {
//                return "";
//            }
//
//            declaredField = (Field)var4.next();
//            name = declaredField.getName();
//        } while(!StringUtil.isNotBlank(fieldName) || !fieldName.replace("_", "").equalsIgnoreCase(name));
//
//        String typeStr = (String)DynamicTableConst.HANDLE_TYPE_MAP.get(declaredField.getGenericType().getTypeName());
//        return StringUtil.isNotBlank(typeStr) ? name + typeStr : name;
//    }
//
//    public InsertBatchSomeColumn setPredicate(final Predicate<TableFieldInfo> predicate) {
//        this.predicate = predicate;
//        return this;
//    }
//}
//
