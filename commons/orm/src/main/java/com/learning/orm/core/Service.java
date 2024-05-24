package com.learning.orm.core;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
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
public interface Service<T> extends IService<T> {

    boolean insertBatch(List<T> entityList, TableParamDto dto);

    default boolean yhSaveOrUpdateBatch(List<T> entityList, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhSaveOrUpdateBatchIgnoreTenant(entityList, dto);
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

    default boolean saveOrUpdateBatch(List<T> entityList, List<String> fieldNameList) {
        DynamicTableUtil.setTenantValue();
        return this.saveOrUpdateBatchIgnoreTenant(entityList, fieldNameList);
    }

    boolean saveOrUpdateBatchNoTFIgnoreTenant(List<T> entityList, TableParamDto dto);

    boolean insert(T entity, TableParamDto dto);

    default boolean delete(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhDeleteIgnoreTenant(queryWrapper, dto);
    }

    boolean yhDeleteIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default boolean removeById(Serializable id, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhRemoveByIdIgnoreTenant(id, dto);
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
        return this.yhUpdateByIdIgnoreTenant(entity, dto);
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
                throw new ServiceException("获取字段信息异常", var10);
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
        return this.yhSaveOrUpdateIgnoreTenant(entity, dto);
    }

    boolean yhSaveOrUpdateIgnoreTenant(T entity, TableParamDto dto);

    default boolean update(T entity, Wrapper<T> updateWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhUpdateIgnoreTenant(entity, updateWrapper, dto);
    }

    default boolean update(T entity, Wrapper<T> updateWrapper, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.update(entity, updateWrapper, dto);
    }

    boolean yhUpdateIgnoreTenant(T entity, Wrapper<T> updateWrapper, TableParamDto dto);

    default boolean updateIgnoreTenant(T entity, Wrapper<T> updateWrapper, TableParamDto dto, Boolean useStock) {
        TableThreadLocalUtil.setUseStock(useStock);
        return this.yhUpdateIgnoreTenant(entity, updateWrapper, dto);
    }

    default boolean updateBatchById(List<T> entityList, int batchSize, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhUpdateBatchByIdIgnoreTenant(entityList, batchSize, dto);
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
        return this.yhGetByIdIgnoreTenant(id, dto);
    }

    default T getByIdWithNoLock(Serializable id, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.getById(id, dto);
    }

    T yhGetByIdIgnoreTenant(Serializable id, TableParamDto dto);

    default T getByIdWithNoLockIgnoreTenant(Serializable id, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhGetByIdIgnoreTenant(id, dto);
    }

    default T getOne(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.getOneIgnoreTenant(queryWrapper, throwEx, dto);
    }

    default T getOne(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.yhGetOne(queryWrapper, false, dto);
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
        return this.yhGetOneIgnoreTenant(queryWrapper, false, dto);
    }

    default T yhGetOneWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.yhGetOneWithNoLockIgnoreTenant(queryWrapper, false, dto);
    }

    default T yhGetOneWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhGetOneIgnoreTenant(queryWrapper, throwEx, dto);
    }

    default Map<String, Object> yhGetMap(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhGetMapIgnoreTenant(queryWrapper, dto);
    }

    default Map<String, Object> yhGetMapWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhGetMap(queryWrapper, dto);
    }

    Map<String, Object> yhGetMapIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default Map<String, Object> yhGetMapWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhGetMapIgnoreTenant(queryWrapper, dto);
    }

    default List<T> yhList(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhListIgnoreTenant(queryWrapper, dto);
    }

    default List<T> yhList(TableParamDto dto) {
        return this.yhList(Wrappers.emptyWrapper(), dto);
    }

    default List<T> yhListWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhList(queryWrapper, dto);
    }

    default List<T> yhListWithNoLock(TableParamDto dto) {
        return this.yhListWithNoLock(Wrappers.emptyWrapper(), dto);
    }

    List<T> yhListIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default List<T> yhListIgnoreTenant(TableParamDto dto) {
        return this.yhListIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<T> yhListWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListIgnoreTenant(queryWrapper, dto);
    }

    default List<T> yhListWithNoLockIgnoreTenant(TableParamDto dto) {
        return this.yhListWithNoLockIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default IPage<T> yhPage(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhPageIgnoreTenant(page, queryWrapper, dto);
    }

    default IPage<T> yhPage(IPage<T> page, TableParamDto dto) {
        return this.yhPage(page, Wrappers.emptyWrapper(), dto);
    }

    default IPage<T> yhPageWithNoLock(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhPage(page, queryWrapper, dto);
    }

    default IPage<T> yhPageWithNoLock(IPage<T> page, TableParamDto dto) {
        return this.yhPageWithNoLock(page, Wrappers.emptyWrapper(), dto);
    }

    IPage<T> yhPageIgnoreTenant(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto);

    default IPage<T> yhPageIgnoreTenant(IPage<T> page, TableParamDto dto) {
        return this.yhPageIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default IPage<T> yhPageWithNoLockIgnoreTenant(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhPageIgnoreTenant(page, queryWrapper, dto);
    }

    default IPage<T> yhPageWithNoLockIgnoreTenant(IPage<T> page, TableParamDto dto) {
        return this.yhPageWithNoLockIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default long yhCount(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhCountIgnoreTenant(queryWrapper, dto);
    }

    default long yhCountWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhCount(queryWrapper, dto);
    }

    default boolean yhExist(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.yhCount(queryWrapper, dto) > 0L;
    }

    default boolean yhExistWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhExist(queryWrapper, dto);
    }

    long yhCountIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default long yhCountWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhCountIgnoreTenant(queryWrapper, dto);
    }

    default boolean yhExistIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        return this.yhCountIgnoreTenant(queryWrapper, dto) > 0L;
    }

    default boolean yhExistWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhExistIgnoreTenant(queryWrapper, dto);
    }

    default List<Map<String, Object>> yhListMaps(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhListMapsIgnoreTenant(queryWrapper, dto);
    }

    default List<Map<String, Object>> listMaps(TableParamDto dto) {
        return this.yhListMaps(Wrappers.emptyWrapper(), dto);
    }

    default List<Map<String, Object>> yhListMapsWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListMaps(queryWrapper, dto);
    }

    default List<Map<String, Object>> listMapsWithNoLock(TableParamDto dto) {
        return this.yhListMapsWithNoLock(Wrappers.emptyWrapper(), dto);
    }

    List<Map<String, Object>> yhListMapsIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default List<Map<String, Object>> listMapsIgnoreTenant(TableParamDto dto) {
        return this.yhListMapsIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<Map<String, Object>> yhListMapsWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListMapsIgnoreTenant(queryWrapper, dto);
    }

    default List<Map<String, Object>> listMapsWithNoLockIgnoreTenant(TableParamDto dto) {
        return this.yhListMapsWithNoLockIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<Object> yhListObjs(Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhListObjsIgnoreTenant(queryWrapper, dto);
    }

    default List<Object> yhListObjs(TableParamDto dto) {
        return this.yhListObjs(Wrappers.emptyWrapper(), dto);
    }

    default List<Object> yhListObjsWithNoLock(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListObjs(queryWrapper, dto);
    }

    default List<Object> yhListObjsWithNoLock(TableParamDto dto) {
        return this.yhListObjsWithNoLock(Wrappers.emptyWrapper(), dto);
    }

    List<Object> yhListObjsIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto);

    default List<Object> yhListObjsIgnoreTenant(TableParamDto dto) {
        return this.yhListObjsIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default List<Object> yhListObjsWithNoLockIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListObjsIgnoreTenant(queryWrapper, dto);
    }

    default List<Object> yhListObjsWithNoLockIgnoreTenant(TableParamDto dto) {
        return this.yhListObjsWithNoLockIgnoreTenant(Wrappers.emptyWrapper(), dto);
    }

    default <E extends IPage<Map<String, Object>>> E yhPageMaps(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhPageMapsIgnoreTenant(page, queryWrapper, dto);
    }

    default <E extends IPage<Map<String, Object>>> E yhPageMaps(E page, TableParamDto dto) {
        return this.yhPageMaps(page, Wrappers.emptyWrapper(), dto);
    }

    default <E extends IPage<Map<String, Object>>> E yhPageMapsWithNoLock(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhPageMaps(page, queryWrapper, dto);
    }

    default <E extends IPage<Map<String, Object>>> E yhPageMapsWithNoLock(E page, TableParamDto dto) {
        return this.yhPageMapsWithNoLock(page, Wrappers.emptyWrapper(), dto);
    }

    <E extends IPage<Map<String, Object>>> E yhPageMapsIgnoreTenant(E page, Wrapper<T> queryWrapper, TableParamDto dto);

    default <E extends IPage<Map<String, Object>>> E yhPageMapsIgnoreTenant(E page, TableParamDto dto) {
        return this.yhPageMapsIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default <E extends IPage<Map<String, Object>>> E yhPageMapsWithNoLockIgnoreTenant(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhPageMapsIgnoreTenant(page, queryWrapper, dto);
    }

    default <E extends IPage<Map<String, Object>>> E yhPageMapsWithNoLockIgnoreTenant(E page, TableParamDto dto) {
        return this.yhPageMapsWithNoLockIgnoreTenant(page, Wrappers.emptyWrapper(), dto);
    }

    default List<T> yhListByMap(Map<String, Object> columnMap, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhListByMapIgnoreTenant(columnMap, dto);
    }

    List<T> yhListByMapIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto);

    default List<T> yhListByMapWithNoLock(Map<String, Object> columnMap, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListByMap(columnMap, dto);
    }

    default List<T> yhListByMapWithNoLockIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListByMapIgnoreTenant(columnMap, dto);
    }

    default List<T> yhListByIds(List<? extends Serializable> idList, TableParamDto dto) {
        DynamicTableUtil.setTenantValue();
        return this.yhListByIdsIgnoreTenant(idList, dto);
    }

    default List<T> yhListByIdsWithNoLock(List<? extends Serializable> idList, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListByIds(idList, dto);
    }

    List<T> yhListByIdsIgnoreTenant(List<? extends Serializable> idList, TableParamDto dto);

    default List<T> yhListByIdsWithNoLockIgnoreTenant(List<? extends Serializable> idList, TableParamDto dto) {
        TableThreadLocalUtil.setUseWithNoLock(true);
        return this.yhListByIdsIgnoreTenant(idList, dto);
    }
}
