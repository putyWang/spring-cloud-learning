//package com.learning.orm.injector;
//
//import com.baomidou.mybatisplus.core.enums.SqlMethod;
//import com.baomidou.mybatisplus.core.injector.AbstractMethod;
//import com.baomidou.mybatisplus.core.metadata.TableInfo;
//import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
//import com.learning.orm.config.UpdateBatchPatch;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlSource;
//import org.springframework.util.StringUtils;
//
///**
// * @ClassName: YhUpdateBatchSomeColumn
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//public class UpdateBatchSomeColumn extends AbstractMethod {
//
//
//    protected UpdateBatchSomeColumn(String methodName) {
//        super(methodName);
//    }
//
//    @Override
//    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
//        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
//        String compsiteId = UpdateBatchPatch.TABLE_COMPSITE_ID.get(tableInfo.getTableName());
//        String additional = this.optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
//        String sql = "";
//        if (StringUtils.isEmpty(compsiteId)) {
//            sql = String.format("UPDATE %s %s WHERE (%s=#{%s}) %s", tableInfo.getTableName(), this.sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, "et", "et."), tableInfo.getKeyColumn(), "et." + tableInfo.getKeyProperty(), additional);
//        } else {
//            String[] split = compsiteId.split("\\|");
//            String idWhere = "";
//            String[] var10 = split;
//            int var11 = split.length;
//
//            for(int var12 = 0; var12 < var11; ++var12) {
//                String s = var10[var12];
//                idWhere = idWhere + s.trim() + "=#{" + "et." + s + "} and ";
//            }
//
//            idWhere = idWhere.substring(0, idWhere.length() - 4);
//            sql = String.format("UPDATE %s %s WHERE (%s) %s", tableInfo.getTableName(), this.sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, "et", "et."), idWhere, additional);
//        }
//
//        String list = "<script>\n" + SqlScriptUtils.convertForeach(sql, "list", (String)null, "et", ";sqlend;") + "\n</script>";
//        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, list, modelClass);
//        return this.addUpdateMappedStatement(mapperClass, modelClass, this.getMethod(sqlMethod), sqlSource);
//    }
//
//    public String getMethod(SqlMethod sqlMethod) {
//        return "updateBatchSomeColumn";
//    }
//}
