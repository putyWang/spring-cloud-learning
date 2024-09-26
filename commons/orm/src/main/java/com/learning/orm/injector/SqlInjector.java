//package com.learning.orm.injector;
//
//import com.baomidou.mybatisplus.core.injector.AbstractMethod;
//import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
//import com.baomidou.mybatisplus.core.metadata.TableInfo;
//
//import java.util.List;
//
///**
// * @ClassName: YhSqlInjector
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//public class SqlInjector extends DefaultSqlInjector {
//
//    /**
//     * 批量插入个别字段方法名
//     */
//    private static final String INSERT_BATCH_SOME_COLUMN_NAME = "InsertBatchSomeColumn";
//
//    /**
//     * 批量更新个别字段方法名
//     */
//    private static final String UPDATE_BATCH_SOME_COLUMN_NAME = "InsertBatchSomeColumn";
//
//    @Override
//    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
//        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
//        methodList.add(new InsertBatchSomeColumn(INSERT_BATCH_SOME_COLUMN_NAME));
//        methodList.add(new UpdateBatchSomeColumn(UPDATE_BATCH_SOME_COLUMN_NAME));
//        return methodList;
//    }
//}
