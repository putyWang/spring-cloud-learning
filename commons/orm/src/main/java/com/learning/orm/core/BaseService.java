package com.learning.orm.core;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.core.exception.SpringBootException;
import com.learning.core.utils.StringUtil;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.dto.TableParamDto;
import com.learning.orm.utils.DynamicTableUtil;
import com.learning.orm.utils.TableThreadLocalUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName: Service
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public interface BaseService<T> extends IService<T> {

    boolean saveBatch(List<T> entityList, TableParamDto dto);

    default boolean saveOrUpdateBatch(List<T> entityList, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.saveOrUpdateBatchIgnoreTenant(entityList, dto);
    }

    default boolean saveOrUpdateBatch(List<T> entityList) {
        return this.saveOrUpdateBatch(entityList, false);
    }

    default boolean saveOrUpdateBatch(List<T> entityList, boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.saveOrUpdateBatchNoTFIgnoreTenant(entityList);
    }

    default boolean saveOrUpdateBatchIgnoreTenant(List<T> entityList) {
        return this.saveOrUpdateBatchIgnoreTenant(entityList, false);
    }

    default boolean saveOrUpdateBatchIgnoreTenant(List<T> entityList, boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.saveOrUpdateBatchNoTFIgnoreTenant(entityList);
    }

    boolean saveOrUpdateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList);

    boolean saveOrUpdateBatchIgnoreTenant(List<T> entityList, TableParamDto dto);

    default boolean saveOrUpdateBatch(List<T> entityList, List<String> fieldNameList) {
        DynamicTableUtil.setTenantValue();
        return this.saveOrUpdateBatchIgnoreTenant(entityList, fieldNameList);
    }

    boolean saveOrUpdateBatchNoTFIgnoreTenant(List<T> entityList);

    boolean insert(T entity, TableParamDto dto);

    default boolean delete(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.deleteIgnoreTenant(queryWrapper, dto);
    }

    boolean deleteIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default boolean removeById(Serializable id, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.removeByIdIgnoreTenant(id, dto);
    }

    default boolean removeByMap(Map<String, Object> columnMap, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.removeByMapIgnoreTenant(columnMap, dto);
    }

    default boolean removeByIds(Collection<? extends Serializable> idList, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.removeByIdsIgnoreTenant(idList, dto);
    }

    boolean removeByIdIgnoreTenant(Serializable id, TableParamDto dto);

    boolean removeByMapIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto);

    boolean removeByIdsIgnoreTenant(Collection<? extends Serializable> idList, TableParamDto dto);

    default boolean updateById(T entity, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.updateByIdIgnoreTenant(entity, dto);
    }

    default boolean updateById(T entity, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.updateById(entity, dto);
    }

    boolean updateByIdIgnoreTenant(T entity, TableParamDto dto);

    default boolean updateByIdIgnoreTenant(T entity, TableParamDto dto, boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.updateByIdIgnoreTenant(entity, dto);
    }

    default boolean updateBatch(List<T> entityList, List<String> fieldNameList, boolean useStock) {
        return this.updateBatch(entityList, fieldNameList, 1000, useStock);
    }

    default boolean updateBatch(List<T> entityList, List<String> fieldNameList, int batchSize) {
        return this.updateBatch(entityList, fieldNameList, batchSize, false);
    }

    default boolean updateBatch(List<T> entityList, List<String> fieldNameList) {
        return this.updateBatch(entityList, fieldNameList, 1000, false);
    }

    default boolean updateBatch(List<T> entityList, List<String> fieldNameList, int batchSize, boolean useStock) {
        DynamicTableUtil.setTenantValue();
        return this.updateBatchIgnoreTenant(entityList, fieldNameList, batchSize, useStock);
    }

    default boolean updateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList, boolean useStock) {
        return this.updateBatchIgnoreTenant(entityList, fieldNameList, 1000, useStock);
    }

    default boolean updateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList, int batchSize) {
        return this.updateBatchIgnoreTenant(entityList, fieldNameList, batchSize, false);
    }

    default boolean updateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList) {
        return this.updateBatchIgnoreTenant(entityList, fieldNameList, 1000, false);
    }

    boolean updateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList, int batchSize, boolean useStock);

    default Wrapper<T> handleCompositeKeys(T entity, String primaryKeys) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        List<Field> allField = DynamicTableUtil.getAllField(entity.getClass());
        Iterator var5 = allField.iterator();

        while(var5.hasNext()) {
            Field declaredField = (Field)var5.next();
            declaredField.setAccessible(true);

            try {
                TableField annotation = (TableField)declaredField.getAnnotation(TableField.class);
                String fieldName;
                if (!Objects.isNull(annotation) && StringUtil.isNotBlank(annotation.value())) {
                    fieldName = annotation.value();
                } else {
                    fieldName = declaredField.getName();
                }

                Object o = declaredField.get(entity);
                if (primaryKeys.contains(fieldName)) {
                    qw.eq(fieldName, o);
                }
            } catch (IllegalAccessException var10) {
                throw new SpringBootException("获取字段信息异常", var10);
            }
        }

        return qw;
    }

    default String getMaintainPrimaryKeys(T entity) {
        DynamicTableUtil.initTableInfoByClass(entity.getClass());
        TableInfoDto table = TableThreadLocalUtil.getTableInfo();
        return table.getPrimaryKey();
    }

    default boolean saveOrUpdate(T entity, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.saveOrUpdateIgnoreTenant(entity, dto);
    }

    boolean saveOrUpdateIgnoreTenant(T entity, TableParamDto dto);

    default boolean update(T entity, Wrapper<T> updateWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.updateIgnoreTenant(entity, updateWrapper, dto);
    }

    default boolean update(T entity, Wrapper<T> updateWrapper, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.update(entity, updateWrapper, dto);
    }

    boolean updateIgnoreTenant(T entity, Wrapper<T> updateWrapper, TableParamDto dto);

    default boolean updateIgnoreTenant(T entity, Wrapper<T> updateWrapper, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.updateIgnoreTenant(entity, updateWrapper, dto);
    }

    default boolean updateBatchById(List<T> entityList, int batchSize, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.updateBatchByIdIgnoreTenant(entityList, batchSize, dto);
    }

    default boolean updateBatchById(List<T> entityList, TableParamDto dto) {
        return this.updateBatchById(entityList, 1000, dto);
    }

    default boolean updateBatchById(List<T> entityList, int batchSize, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.updateBatchById(entityList, batchSize, dto);
    }

    default boolean updateBatchByIdIgnoreTenant(List<T> entityList, TableParamDto dto) {
        return this.updateBatchByIdIgnoreTenant(entityList, 1000, dto);
    }

    boolean updateBatchByIdIgnoreTenant(List<T> entityList, int batchSize, TableParamDto dto);

    default boolean updateBatchByIdIgnoreTenant(List<T> entityList, int batchSize, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.updateBatchByIdIgnoreTenant(entityList, batchSize, dto);
    }

    default T getById(Serializable id, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.getByIdIgnoreTenant(id, dto);
    }

    default T getByIdWithNoLock(Serializable id, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getById(id, dto);
    }

    T getByIdIgnoreTenant(Serializable id, TableParamDto dto);

    default T getByIdWithNoLockIgnoreTenant(Serializable id, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getByIdIgnoreTenant(id, dto);
    }

    default T getOne(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.getOneIgnoreTenant(queryWrapper, throwEx, dto);
    }

    default T getOne(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.getOne(queryWrapper, false, dto);
    }

    default T getOneWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.getOneWithNoLock(queryWrapper, false, dto);
    }

    default T getOneWithNoLock(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getOne(queryWrapper, throwEx, dto);
    }

    T getOneIgnoreTenant(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto);

    default T getOneIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.getOneIgnoreTenant(queryWrapper, false, dto);
    }

    default T getOneWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.getOneWithNoLockIgnoreTenant(queryWrapper, false, dto);
    }

    default T getOneWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getOneIgnoreTenant(queryWrapper, throwEx, dto);
    }

    default Map<String, Object> getMap(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.getMapIgnoreTenant(queryWrapper, dto);
    }

    default Map<String, Object> getMapWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getMap(queryWrapper, dto);
    }

    Map<String, Object> getMapIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default Map<String, Object> GetMapWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getMapIgnoreTenant(queryWrapper, dto);
    }

    default List<T> selectList(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectListIgnoreTenant(queryWrapper, dto);
    }

    default List<T> selectList(TableParamDto dto) {
        return this.selectList(Wrappers.emptyWrapper(), dto);
    }

    default List<T> selectListWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectList(queryWrapper, dto);
    }

    default List<T> selectListWithNoLock(TableParamDto dto) {
        return this.selectListWithNoLock(Wrappers.emptyWrapper(), dto);
    }

    List<T> selectListIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default List<T> selectListIgnoreTenant(TableParamDto dto) {
        return this.selectListIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<T> selectListWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListIgnoreTenant(queryWrapper, dto);
    }

    default List<T> selectListWithNoLockIgnoreTenant(TableParamDto dto) {
        return this.selectListWithNoLockIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectPageIgnoreTenant(page, queryWrapper, dto);
    }

    default IPage<T> selectPage(IPage<T> page, TableParamDto dto) {
        return this.selectPage(page, Wrappers.emptyWrapper(), dto);
    }

    default IPage<T> selectPageWithNoLock(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectPage(page, queryWrapper, dto);
    }

    default IPage<T> selectPageWithNoLock(IPage<T> page, TableParamDto dto) {
        return this.selectPageWithNoLock(page, Wrappers.emptyWrapper(), dto);
    }

    IPage<T> selectPageIgnoreTenant(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto);

    default IPage<T> selectPageIgnoreTenant(IPage<T> page, TableParamDto dto) {
        return this.selectPageIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default IPage<T> selectPageWithNoLockIgnoreTenant(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectPageIgnoreTenant(page, queryWrapper, dto);
    }

    default IPage<T> selectPageWithNoLockIgnoreTenant(IPage<T> page, TableParamDto dto) {
        return this.selectPageWithNoLockIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default long selectCount(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectCountIgnoreTenant(queryWrapper, dto);
    }

    default long selectCountWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectCount(queryWrapper, dto);
    }

    default boolean isExist(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.selectCount(queryWrapper, dto) > 0L;
    }

    default boolean isExistWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.isExist(queryWrapper, dto);
    }

    long selectCountIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default long selectCountWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectCountIgnoreTenant(queryWrapper, dto);
    }

    default boolean isExistIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.selectCountIgnoreTenant(queryWrapper, dto) > 0L;
    }

    default boolean isExistWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.isExistIgnoreTenant(queryWrapper, dto);
    }

    default List<Map<String, Object>> selectListMaps(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectListMapsIgnoreTenant(queryWrapper, dto);
    }

    default List<Map<String, Object>> selectListMaps(TableParamDto dto) {
        return this.selectListMaps(Wrappers.emptyWrapper(), dto);
    }

    default List<Map<String, Object>> selectListMapsWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListMaps(queryWrapper, dto);
    }

    default List<Map<String, Object>> selectListMapsWithNoLock(TableParamDto dto) {
        return this.selectListMapsWithNoLock(Wrappers.emptyWrapper(), dto);
    }

    List<Map<String, Object>> selectListMapsIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default List<Map<String, Object>> selectListMapsIgnoreTenant(TableParamDto dto) {
        return this.selectListMapsIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<Map<String, Object>> selectListMapsWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListMapsIgnoreTenant(queryWrapper, dto);
    }

    default List<Map<String, Object>> listMapsWithNoLockIgnoreTenant(TableParamDto dto) {
        return this.selectListMapsWithNoLockIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<Object> selectListObjs(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectListObjsIgnoreTenant(queryWrapper, dto);
    }

    default List<Object> selectListObjs(TableParamDto dto) {
        return this.selectListObjs(Wrappers.emptyWrapper(), dto);
    }

    default List<Object> selectListObjsWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListObjs(queryWrapper, dto);
    }

    default List<Object> selectListObjsWithNoLock(TableParamDto dto) {
        return this.selectListObjsWithNoLock(Wrappers.emptyWrapper(), dto);
    }

    List<Object> selectListObjsIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default List<Object> selectListObjsIgnoreTenant(TableParamDto dto) {
        return this.selectListObjsIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<Object> selectListObjsWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListObjsIgnoreTenant(queryWrapper, dto);
    }

    default List<Object> selectListObjsWithNoLockIgnoreTenant(TableParamDto dto) {
        return this.selectListObjsWithNoLockIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default <E extends IPage<Map<String, Object>>> E selectPageMaps(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectPageMapsIgnoreTenant(page, queryWrapper, dto);
    }

    default <E extends IPage<Map<String, Object>>> E selectPageMaps(E page, TableParamDto dto) {
        return this.selectPageMaps(page, Wrappers.emptyWrapper(), dto);
    }

    default <E extends IPage<Map<String, Object>>> E selectPageMapsWithNoLock(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectPageMaps(page, queryWrapper, dto);
    }

    default <E extends IPage<Map<String, Object>>> E selectPageMapsWithNoLock(E page, TableParamDto dto) {
        return this.selectPageMapsWithNoLock(page, Wrappers.emptyWrapper(), dto);
    }

    <E extends IPage<Map<String, Object>>> E selectPageMapsIgnoreTenant(E page, Wrapper<T> queryWrapper, TableParamDto dto);

    default <E extends IPage<Map<String, Object>>> E selectPageMapsIgnoreTenant(E page, TableParamDto dto) {
        return this.selectPageMapsIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default <E extends IPage<Map<String, Object>>> E selectPageMapsWithNoLockIgnoreTenant(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectPageMapsIgnoreTenant(page, queryWrapper, dto);
    }

    default <E extends IPage<Map<String, Object>>> E selectPageMapsWithNoLockIgnoreTenant(E page, TableParamDto dto) {
        return this.selectPageMapsWithNoLockIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default List<T> selectListByMap(Map<String, Object> columnMap, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectListByMapIgnoreTenant(columnMap, dto);
    }

    List<T> selectListByMapIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto);

    default List<T> selectListByMapWithNoLock(Map<String, Object> columnMap, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListByMap(columnMap, dto);
    }

    default List<T> selectListByMapWithNoLockIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListByMapIgnoreTenant(columnMap, dto);
    }

    default List<T> selectListByIds(List<? extends Serializable> idList, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.selectListByIdsIgnoreTenant(idList, dto);
    }

    default List<T> selectListByIdsWithNoLock(List<? extends Serializable> idList, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListByIds(idList, dto);
    }

    List<T> selectListByIdsIgnoreTenant(List<? extends Serializable> idList, TableParamDto dto);

    default List<T> selectListByIdsWithNoLockIgnoreTenant(List<? extends Serializable> idList, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.selectListByIdsIgnoreTenant(idList, dto);
    }
}
