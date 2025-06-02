//package com.learning.repository;
//
///**
// * @author wangwei
// * @version 1.0
// * @date 2025/6/2 上午12:14
// */
//
//import com.learning.constant.Constants;
//import com.learning.entity.metadata.TableInfo;
//import com.learning.util.TableInfoHelper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collection;
//
///**
// * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ）
// *
// * @author hubin
// * @since 2018-06-23
// */
//public abstract class CrudRepository<M extends ReactiveCrudRepository<T, ?>, T> extends AbstractRepository<M, T> {
//
//    @Autowired
//    protected M baseMapper;
//
//    @Override
//    public M getBaseMapper() {
//        Assert.notNull(this.baseMapper, "baseMapper can not be null");
//        return this.baseMapper;
//    }
//
//    /**
//     * 批量插入
//     *
//     * @param entityList ignore
//     * @param batchSize  ignore
//     * @return ignore
//     */
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean saveBatch(Collection<T> entityList, int batchSize) {
//        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
//        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
//    }
//
//    /**
//     * 获取mapperStatementId
//     *
//     * @param sqlMethod 方法名
//     * @return 命名id
//     * @since 3.4.0
//     */
//    protected String getSqlStatement(SqlMethod sqlMethod) {
//        return SqlHelper.getSqlStatement(this.getMapperClass(), sqlMethod);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
//        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
//        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
//        String keyProperty = tableInfo.getKeyProperty();
//        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
//        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), this.getMapperClass(), this.log, entityList, batchSize, (sqlSession, entity) -> {
//            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
//            return StringUtils.checkValNull(idVal)
//                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
//        }, (sqlSession, entity) -> {
//            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
//            param.put(Constants.ENTITY, entity);
//            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
//        });
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
//        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
//        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
//            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
//            param.put(Constants.ENTITY, entity);
//            sqlSession.update(sqlStatement, param);
//        });
//    }
//}
//
