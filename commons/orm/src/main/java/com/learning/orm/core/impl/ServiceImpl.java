package com.learning.orm.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.io.grpc.internal.JsonUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.learning.core.utils.StringUtil;
import com.learning.orm.annotation.TableCode;
import com.learning.orm.config.OrmUtilConfig;
import com.learning.orm.dto.TableAnnotationDto;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.dto.TableParamDto;
import com.learning.orm.entity.YhEntity;
import com.learning.orm.enums.TableTypeEnum;
import com.learning.orm.mapper.RootMapper;
import com.learning.orm.utils.DynamicTableUtil;
import com.learning.orm.utils.TableThreadLocalUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName: ServiceImpl
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class ServiceImpl<M extends RootMapper<T>, T> extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T> implements YhService<T> {
    private static final Integer MAX_PARAMS = 2099;

    private void handleBatchMapData(Map<TableParamDto, List<T>> map, TableParamDto tableParamDto, T t) {
        YhEntity yhEntity;
        if (CollectionUtils.isEmpty((Collection)map.get(tableParamDto))) {
            List<T> list = new ArrayList();
            if (t instanceof YhEntity) {
                yhEntity = (YhEntity)t;
                yhEntity.initDefault();
            }

            list.add(t);
            map.put(tableParamDto, list);
        } else {
            List<T> list = (List)map.get(tableParamDto);
            if (t instanceof YhEntity) {
                yhEntity = (YhEntity)t;
                yhEntity.initDefault();
            }

            list.add(t);
        }

    }

    private int yhInsertBatchSomeColumnMap(Map<TableParamDto, List<T>> insertMap, int batchSize) {
        int insertCount = 0;

        TableParamDto key;
        List value;
        for(Iterator var4 = insertMap.entrySet().iterator(); var4.hasNext(); insertCount += this.slicingHandleList(value, batchSize, key)) {
            Map.Entry<TableParamDto, List<T>> map = (Map.Entry)var4.next();
            key = (TableParamDto)map.getKey();
            value = (List)map.getValue();
        }

        return insertCount;
    }

    private int slicingHandleList(List<T> entityList, int batchSize, TableParamDto tableParamDto) {
        int insertCount = 0;
        List<List<T>> partition = Lists.partition(entityList, batchSize);

        List list;
        for(Iterator var6 = partition.iterator(); var6.hasNext(); insertCount += this.insertBatchSomeColumn(list, tableParamDto)) {
            list = (List)var6.next();
        }

        return insertCount;
    }

    private int insertBatchSomeColumn(Collection<T> entityList, TableParamDto dto) {
        int var3;
        try {
            this.setTableParam(dto);
            var3 = ((YhMapper)this.getBaseMapper()).insertBatchSomeColumn(entityList);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    private TableParamDto handleSaveOrUpdateBatch(T t, TableCode tableCode, Map<String, Object> idMap, Map<String, Object> tableIdMap, QueryWrapper<T> qw) {
        Map<String, Object> map = new HashMap(3);
        Integer iType = null;
        List<Field> fieldList = DynamicTableUtil.getAllField(t.getClass());
        Iterator var9 = fieldList.iterator();

        while(var9.hasNext()) {
            Field field = (Field)var9.next();
            field.setAccessible(true);

            try {
                Object o = field.get(t);
                if (!Objects.isNull(tableCode)) {
                    TableInfoDto tableInfo = this.getTableInfoDto(t);
                    String primaryKey = tableInfo.getPrimaryKey();
                    iType = tableInfo.getIType();
                    TableField annotation = (TableField)field.getAnnotation(TableField.class);
                    String fieldName;
                    if (!Objects.isNull(annotation) && StringUtil.isNotBlank(annotation.value())) {
                        fieldName = annotation.value();
                    } else {
                        fieldName = field.getName();
                    }

                    if (Objects.equals(fieldName, tableInfo.getDateKey())) {
                        map.put("dateValue", o);
                    }

                    if (Objects.equals(fieldName, tableInfo.getPartitionKey())) {
                        map.put("partitionValue", o);
                    }

                    if (Objects.equals(fieldName, tableInfo.getWardKey())) {
                        map.put("wardValue", o);
                    }

                    if (!Objects.isNull(idMap) && StringUtil.isNotBlank(primaryKey) && primaryKey.contains("|") && primaryKey.contains(fieldName)) {
                        idMap.put(fieldName, o);
                    }
                }

                if (!Objects.isNull(tableIdMap)) {
                    TableId tableId = (TableId)field.getAnnotation(TableId.class);
                    if (!Objects.isNull(tableId)) {
                        tableIdMap.put(StringUtil.isBlank(tableId.value()) ? field.getName() : tableId.value(), o);
                    }
                }
            } catch (IllegalAccessException var16) {
                throw new ServiceException("获取字段信息异常", var16);
            }
        }

        if (!Objects.isNull(qw) && !Objects.isNull(idMap) && !Objects.isNull(tableIdMap)) {
            this.handlePrimaryKey(idMap, tableIdMap, qw);
        }

        return Objects.isNull(tableCode) ? null : DynamicTableUtil.getTableParamDtoByTableAnnotationDto((TableAnnotationDto) JsonUtil.parseObject(JsonUtil.toJson(map), TableAnnotationDto.class), iType);
    }

    private TableParamDto handleSaveOrUpdateBatch2(T t, List<String> fieldNameList, QueryWrapper<T> qw) {
        Map<String, Object> map = new HashMap(3);
        Integer iType = null;
        List<Field> fieldList = DynamicTableUtil.getAllField(t.getClass());
        Iterator var7 = fieldList.iterator();

        while(var7.hasNext()) {
            Field field = (Field)var7.next();
            field.setAccessible(true);

            try {
                Object o = field.get(t);
                TableInfoDto tableInfo = this.getTableInfoDto(t);
                iType = tableInfo.getIType();
                String fieldName = this.getFieldName(field);
                if (fieldNameList.contains(fieldName)) {
                    qw.eq(fieldName, o);
                }
            } catch (IllegalAccessException var12) {
                throw new ServiceException("获取字段信息异常", var12);
            }
        }

        return DynamicTableUtil.getTableParamDtoByTableAnnotationDto((TableAnnotationDto)JsonUtil.parseObject(JsonUtil.toJson(map), TableAnnotationDto.class), iType);
    }

    private String getFieldName(Field field) {
        TableField annotation = (TableField)field.getAnnotation(TableField.class);
        return !Objects.isNull(annotation) && StringUtil.isNotBlank(annotation.value()) ? annotation.value() : field.getName();
    }

    private TableParamDto handleSaveBatch(T t, TableCode tableCode) {
        return this.handleSaveOrUpdateBatch(t, tableCode, (Map)null, (Map)null, (QueryWrapper)null);
    }

    private TableInfoDto getTableInfoDto(T t) {
        OrmUtilConfig.initTableInfoByTableCode(((TableCode)t.getClass().getAnnotation(TableCode.class)).value());
        return TableThreadLocalUtil.getTableInfo();
    }

    private void handlePrimaryKey(Map<String, Object> idMap, Map<String, Object> tableIdMap, QueryWrapper<T> qw) {
        Iterator var4;
        Map.Entry idMap1;
        if (CollectionUtil.isNotEmpty(idMap)) {
            var4 = idMap.entrySet().iterator();

            while(var4.hasNext()) {
                idMap1 = (Map.Entry)var4.next();
                qw.eq(idMap1.getKey(), idMap1.getValue());
            }
        } else {
            var4 = tableIdMap.entrySet().iterator();

            while(var4.hasNext()) {
                idMap1 = (Map.Entry)var4.next();
                qw.eq(idMap1.getKey(), idMap1.getValue());
            }
        }

    }

    /** @deprecated */
    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Deprecated
    public boolean yhSaveBatchNoTF(List<T> entityList) {
        Assert.isTrue(CollectionUtil.isNotEmpty(entityList), "error: entityList must not be empty");
        Map<TableParamDto, List<T>> insertMap = new HashMap(entityList.size());
        Class<?> aClass = entityList.get(0).getClass();
        int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
        TableCode tableCode = (TableCode)aClass.getAnnotation(TableCode.class);
        Iterator var6 = entityList.iterator();

        while(var6.hasNext()) {
            T t = var6.next();
            TableParamDto tableParamDto = this.handleSaveBatch(t, tableCode);
            this.handleBatchMapData(insertMap, tableParamDto, t);
        }

        return this.yhInsertBatchSomeColumnMap(insertMap, MAX_PARAMS / batchSizeZero) > 0;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean yhSaveBatch(List<T> entityList, TableParamDto dto) {
        try {
            Assert.isTrue(CollectionUtil.isNotEmpty(entityList), "error: entityList must not be empty");
            Map<TableParamDto, List<T>> insertMap = new HashMap(entityList.size());
            int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
            Iterator var5 = entityList.iterator();

            while(var5.hasNext()) {
                T t = var5.next();
                this.handleBatchMapData(insertMap, dto, t);
            }

            boolean var10 = this.yhInsertBatchSomeColumnMap(insertMap, MAX_PARAMS / batchSizeZero) > 0;
            return var10;
        } finally {
            TableThreadLocalUtil.remove();
        }
    }

    /** @deprecated */
    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Deprecated
    public boolean yhSaveOrUpdateBatchNoTFIgnoreTenant(List<T> entityList) {
        boolean var20;
        try {
            int updateCount = 0;
            Assert.isTrue(CollectionUtil.isNotEmpty(entityList), "error: entityList must not be empty");
            Map<TableParamDto, List<T>> insertMap = new HashMap(entityList.size());
            Map<String, Object> idMap = new HashMap(4);
            Map<String, Object> tableIdMap = new HashMap(1);
            Class<?> aClass = entityList.get(0).getClass();
            int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
            TableCode tableCode = (TableCode)aClass.getAnnotation(TableCode.class);
            List updateList = new ArrayList();
            TableParamDto tableParamDto1 = null;
            Iterator var11 = entityList.iterator();

            while(var11.hasNext()) {
                T t = var11.next();
                QueryWrapper<T> qw = new QueryWrapper();
                TableParamDto tableParamDto = this.handleSaveOrUpdateBatch(t, tableCode, idMap, tableIdMap, qw);
                tableParamDto1 = tableParamDto;
                if (this.privateCount(qw, tableParamDto) > 0L) {
                    updateList.add(t);
                } else {
                    this.handleBatchMapData(insertMap, tableParamDto, t);
                }
            }

            List<List<T>> partition = Lists.partition(updateList, 1000 / batchSizeZero);
            if (tableParamDto1 != null) {
                this.setTableParam(tableParamDto1);
            }

            Iterator var19 = partition.iterator();

            while(var19.hasNext()) {
                List<T> list = (List)var19.next();
                int i = ((YhMapper)this.baseMapper).updateBatchSomeColumn(list);
                if (i > 0) {
                    updateCount += list.size();
                }
            }

            var20 = this.yhInsertBatchSomeColumnMap(insertMap, MAX_PARAMS / batchSizeZero) + updateCount > 0;
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var20;
    }

    public boolean saveOrUpdateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList) {
        boolean var4;
        try {
            int updateCount = 0;
            Assert.isTrue(CollectionUtil.isNotEmpty(entityList), "error: entityList must not be empty");
            if (!CollectionUtil.isEmpty(fieldNameList)) {
                Map<TableParamDto, List<T>> insertMap = new HashMap(entityList.size());
                Class<?> aClass = entityList.get(0).getClass();
                int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
                Iterator var7 = entityList.iterator();

                while(var7.hasNext()) {
                    T t = var7.next();
                    QueryWrapper<T> qw = new QueryWrapper();
                    TableParamDto tableParamDto = this.handleSaveOrUpdateBatch2(t, fieldNameList, qw);
                    if (this.privateCount(qw, tableParamDto) > 0L) {
                        ++updateCount;
                        super.update(t, qw);
                    } else {
                        this.handleBatchMapData(insertMap, tableParamDto, t);
                    }
                }

                boolean var15 = this.yhInsertBatchSomeColumnMap(insertMap, MAX_PARAMS / batchSizeZero) + updateCount > 0;
                return var15;
            }

            var4 = this.yhSaveOrUpdateBatchNoTF(entityList);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean yhSaveOrUpdateBatchIgnoreTenant(List<T> entityList, TableParamDto dto) {
        boolean var29;
        try {
            int updateCount = 0;
            Assert.isTrue(CollectionUtil.isNotEmpty(entityList), "error: entityList must not be empty");
            Map<TableParamDto, List<T>> insertMap = new HashMap(entityList.size());
            Map<String, Object> idMap = new HashMap(4);
            Map<String, Object> tableIdMap = new HashMap(1);
            Class<?> aClass = entityList.get(0).getClass();
            int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
            TableCode tableCode = (TableCode)aClass.getAnnotation(TableCode.class);
            List updateList = new ArrayList();
            Iterator var11 = entityList.iterator();

            while(var11.hasNext()) {
                T t = var11.next();
                QueryWrapper<T> qw = new QueryWrapper();
                List<Field> fieldList = DynamicTableUtil.getAllField(t.getClass());
                Iterator var15 = fieldList.iterator();

                while(var15.hasNext()) {
                    Field field = (Field)var15.next();
                    field.setAccessible(true);

                    try {
                        Object o = field.get(t);
                        TableField annotation = (TableField)field.getAnnotation(TableField.class);
                        String fieldName;
                        if (!Objects.isNull(annotation) && StringUtil.isNotBlank(annotation.value())) {
                            fieldName = annotation.value();
                        } else {
                            fieldName = field.getName();
                        }

                        if (!Objects.isNull(dto)) {
                            OrmUtilConfig.initTableInfoByTableCode(tableCode.value());
                            TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
                            String primaryKey = tableInfo.getPrimaryKey();
                            if (StringUtil.isNotBlank(primaryKey) && primaryKey.contains("|") && primaryKey.contains(fieldName)) {
                                idMap.put(fieldName, o);
                            }
                        }

                        TableId tableId = (TableId)field.getAnnotation(TableId.class);
                        if (!Objects.isNull(tableId)) {
                            tableIdMap.put(StringUtil.isBlank(tableId.value()) ? field.getName() : tableId.value(), o);
                        }
                    } catch (IllegalAccessException var25) {
                        throw new ServiceException("获取字段信息异常", var25);
                    }
                }

                this.handlePrimaryKey(idMap, tableIdMap, qw);
                if (this.privateCount(qw, dto) > 0L) {
                    updateList.add(t);
                } else {
                    this.handleBatchMapData(insertMap, dto, t);
                }
            }

            List<List<T>> partition = Lists.partition(updateList, 1000 / batchSizeZero);
            this.setTableParam(dto);
            Iterator var28 = partition.iterator();

            while(var28.hasNext()) {
                List<T> list = (List)var28.next();
                int i = ((YhMapper)this.baseMapper).updateBatchSomeColumn(list);
                if (i > 0) {
                    updateCount += list.size();
                }
            }

            var29 = this.yhInsertBatchSomeColumnMap(insertMap, MAX_PARAMS / batchSizeZero) + updateCount > 0;
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var29;
    }

    public boolean yhSave(T entity, TableParamDto dto) {
        boolean var7;
        try {
            this.setTableParam(dto);
            if (entity instanceof YhEntity) {
                YhEntity yhEntity = (YhEntity)entity;
                yhEntity.initDefault();
            }

            var7 = super.save(entity);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var7;
    }

    public boolean yhDeleteIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        boolean var3;
        try {
            this.setTableParam(dto);
            var3 = super.remove(queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public boolean yhRemoveByIdIgnoreTenant(Serializable id, TableParamDto dto) {
        boolean var3;
        try {
            this.setTableParam(dto);
            var3 = this.removeById(id);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public boolean yhRemoveByMapIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto) {
        boolean var3;
        try {
            this.setTableParam(dto);
            var3 = this.removeByMap(columnMap);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public boolean yhRemoveByIdsIgnoreTenant(Collection<? extends Serializable> idList, TableParamDto dto) {
        boolean var3;
        try {
            this.setTableParam(dto);
            var3 = this.removeByIds(idList);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public boolean yhUpdateByIdIgnoreTenant(T entity, TableParamDto dto) {
        boolean var4;
        try {
            this.setTableParam(dto);
            String primaryKey = this.getMaintainPrimaryKeys(entity);
            if (!StringUtil.isNotBlank(primaryKey) || !primaryKey.contains("|")) {
                var4 = super.updateById(entity);
                return var4;
            }

            var4 = super.update(entity, this.handleCompositeKeys(entity, primaryKey));
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    public boolean updateBatchIgnoreTenant(List<T> entityList, List<String> fieldNameList, int batchSize, boolean useStock) {
        boolean ret;
        try {
            if (CollectionUtil.isEmpty(entityList)) {
                throw new IllegalArgumentException("Error: entityList must not be empty");
            }

            if (!CollectionUtil.isEmpty(fieldNameList)) {
                TableThreadLocalUtil.setUseStock(useStock);
                ret = true;
                int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
                List<List<T>> partition = Lists.partition(entityList, batchSize / batchSizeZero);

                List list;
                for(Iterator var8 = partition.iterator(); var8.hasNext(); ret = ret && ((YhMapper)this.baseMapper).updateBatchSomeColumn(list) > 0) {
                    list = (List)var8.next();
                }

                boolean var13 = ret;
                return var13;
            }

            ret = this.yhUpdateBatchByIdIgnoreTenant(entityList, batchSize, (TableParamDto)null, useStock);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return ret;
    }

    public boolean yhSaveOrUpdateIgnoreTenant(T entity, TableParamDto dto) {
        boolean var4;
        try {
            this.setTableParam(dto);
            if (entity instanceof YhEntity) {
                YhEntity yhEntity = (YhEntity)entity;
                yhEntity.initDefault();
            }

            String primaryKey = this.getMaintainPrimaryKeys(entity);
            if (!StringUtil.isNotBlank(primaryKey) || !primaryKey.contains("|")) {
                var4 = super.saveOrUpdate(entity);
                return var4;
            }

            var4 = super.update(entity, this.handleCompositeKeys(entity, primaryKey)) || this.yhSave(entity, TableParamDto.initSingleTable());
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    public boolean yhUpdateIgnoreTenant(T entity, Wrapper<T> updateWrapper, TableParamDto dto) {
        boolean var4;
        try {
            this.setTableParam(dto);
            var4 = super.update(entity, updateWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    public boolean yhUpdateBatchByIdIgnoreTenant(List<T> entityList, int batchSize, TableParamDto dto) {
        try {
            if (CollectionUtil.isEmpty(entityList)) {
                throw new IllegalArgumentException("Error: entityList must not be empty");
            } else {
                this.setTableParam(dto);
                this.getMaintainPrimaryKeys(entityList.get(0));
                boolean ret = true;
                int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
                List<List<T>> partition = Lists.partition(entityList, batchSize / batchSizeZero);

                List list;
                for(Iterator var8 = partition.iterator(); var8.hasNext(); ret = ret && ((YhMapper)this.baseMapper).updateBatchSomeColumn(list) > 0) {
                    list = (List)var8.next();
                }

                boolean var13 = ret;
                return var13;
            }
        } finally {
            TableThreadLocalUtil.remove();
        }
    }

    /** @deprecated */
    @Deprecated
    public boolean yhUpdateBatchByIdNoTFIgnoreTenant(List<T> entityList, int batchSize) {
        try {
            if (CollectionUtil.isEmpty(entityList)) {
                throw new IllegalArgumentException("Error: entityList must not be empty");
            } else {
                Class<?> aClass = entityList.get(0).getClass();
                TableCode tableCode = (TableCode)aClass.getAnnotation(TableCode.class);
                this.getMaintainPrimaryKeys(entityList.get(0));
                boolean ret = true;
                int batchSizeZero = DynamicTableUtil.getAllField(entityList.get(0).getClass()).size();
                List<List<T>> partition = Lists.partition(entityList, batchSize / batchSizeZero);

                List list;
                for(Iterator var9 = partition.iterator(); var9.hasNext(); ret = ret && ((YhMapper)this.baseMapper).updateBatchSomeColumn(list) > 0) {
                    list = (List)var9.next();
                }

                boolean var14 = ret;
                return var14;
            }
        } finally {
            TableThreadLocalUtil.remove();
        }
    }

    public T yhGetByIdIgnoreTenant(Serializable id, TableParamDto dto) {
        Object var3;
        try {
            this.setTableParam(dto);
            var3 = super.getById(id);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public T yhGetOneIgnoreTenant(Wrapper<T> queryWrapper, boolean throwEx, TableParamDto dto) {
        Object var4;
        try {
            this.setTableParam(dto);
            var4 = super.getOne(queryWrapper, throwEx);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    public Map<String, Object> yhGetMapIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        Map var3;
        try {
            this.setTableParam(dto);
            var3 = super.getMap(queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public List<T> yhListIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        List var3;
        try {
            this.setTableParam(dto);
            var3 = super.list(queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public IPage<T> yhPageIgnoreTenant(IPage<T> page, Wrapper<T> queryWrapper, TableParamDto dto) {
        IPage var4;
        try {
            this.setTableParam(dto);
            var4 = super.page(page, queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    public long yhCountIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        long var3;
        try {
            this.setTableParam(dto);
            var3 = (long)super.count(queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    private long privateCount(Wrapper<T> queryWrapper, TableParamDto dto) {
        this.setTableParam(dto);
        return (long)super.count(queryWrapper);
    }

    public List<Map<String, Object>> yhListMapsIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        List var3;
        try {
            this.setTableParam(dto);
            var3 = super.listMaps(queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public List<Object> yhListObjsIgnoreTenant(Wrapper<T> queryWrapper, TableParamDto dto) {
        List var3;
        try {
            this.setTableParam(dto);
            var3 = super.listObjs(queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public <E extends IPage<Map<String, Object>>> E yhPageMapsIgnoreTenant(E page, Wrapper<T> queryWrapper, TableParamDto dto) {
        IPage var4;
        try {
            this.setTableParam(dto);
            var4 = super.pageMaps(page, queryWrapper);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var4;
    }

    public List<T> yhListByMapIgnoreTenant(Map<String, Object> columnMap, TableParamDto dto) {
        List var3;
        try {
            this.setTableParam(dto);
            var3 = super.listByMap(columnMap);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    public List<T> yhListByIdsIgnoreTenant(List<? extends Serializable> idList, TableParamDto dto) {
        List var3;
        try {
            this.setTableParam(dto);
            var3 = super.listByIds(idList);
        } finally {
            TableThreadLocalUtil.remove();
        }

        return var3;
    }

    private void setTableParam(TableParamDto dto) {
        if (dto == null) {
            dto = new TableParamDto();
            dto.setTypeEnum(TableTypeEnum.SINGLE_TABLE);
        }

        TableThreadLocalUtil.setTableParam(dto);
    }
}
