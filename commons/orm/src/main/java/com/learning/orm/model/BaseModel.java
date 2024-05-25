package com.learning.orm.model;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.StringUtil;
import com.learning.orm.config.OrmUtilConfig;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.dto.TableParamDto;
import com.learning.orm.enums.TableTypeEnum;
import com.learning.orm.mapper.DynamicSqlMapper;
import com.learning.orm.utils.PoUtil;
import com.learning.orm.utils.TableThreadLocalUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: BaseModel
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Setter
@Getter
public class BaseModel {

    static final DynamicSqlMapper MAPPER;
    public String dataBaseName;
    public String tableName;
    public List<String> fieldList;
    public Map<String, Object> fieldMap;
    public String keys;
    public TableParamDto tableParamDto;

    private static void handleOtherInfo(String dataBaseName, String tableName, TableParamDto tableParamDto) {
        Assert.isTrue(StringUtil.isNotBlank(tableName), "表名不能为空");
        TableInfoDto tableInfoDto = new TableInfoDto();
        tableInfoDto.setDataBaseName(dataBaseName);
        tableInfoDto.setName(tableName);
        TableThreadLocalUtil.setTableParam(tableParamDto);
        TableThreadLocalUtil.setTableInfo(tableInfoDto);
    }

    private static TableInfoDto codeHandle(String code, TableParamDto tableParamDto) {
        Assert.isTrue(StringUtil.isNotBlank(code), "编码不能为空");
        OrmUtilConfig.initTableInfoByTableCode(code);
        TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
        String name = tableInfo.getName();
        Assert.isTrue(StringUtil.isNotBlank(name), "表名不能为空");
        TableThreadLocalUtil.setTableParam(tableParamDto);
        TableThreadLocalUtil.setTableInfo(tableInfo);
        return tableInfo;
    }

    private static TableInfoDto codeHandle(String code, TableParamDto tableParamDto, Map<String, Object> fieldMap) {
        Assert.isTrue(CollectionUtil.isNotEmpty(fieldMap), "字段信息不能为空");
        String join = StringUtils.join(fieldMap.keySet().toArray(), ",");
        Assert.isTrue(PoUtil.isSqlInject(join), String.format("表字段存在sql注入,fields=%s", join));
        Assert.isTrue(StringUtil.isNotBlank(code), "编码不能为空");
        OrmUtilConfig.initTableInfoByTableCode(code);
        TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
        if (Objects.isNull(tableParamDto)) {
            tableParamDto = handleTableParamDto(tableInfo, fieldMap);
        }

        String name = tableInfo.getName();
        Assert.isTrue(StringUtil.isNotBlank(name), "表名不能为空");
        TableThreadLocalUtil.setTableParam(tableParamDto);
        TableThreadLocalUtil.setTableInfo(tableInfo);
        return tableInfo;
    }

    private static TableParamDto handleTableParamDto(TableInfoDto tableInfo, Map<String, Object> fieldMap) {
        Integer type = tableInfo.getIType();
        String dateKey = tableInfo.getDateKey();
        String partitionKey = tableInfo.getPartitionKey();
        String wardKey = tableInfo.getWardKey();
        Object dateValue;
        if (Objects.equals(type, TableTypeEnum.WARD_TABLE.getValue())) {
            dateValue = fieldMap.get(wardKey);
            Assert.notNull(dateValue, "病区表值获取失败,请检查配置或则病区字段是否有值");
            return TableParamDto.initWardParam(dateValue.toString());
        } else if (Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_PARTITION.getValue())) {
            dateValue = fieldMap.get(partitionKey);
            Assert.notNull(dateValue, "单库分区表值获取失败,请检查配置或则分区字段是否有值");
            return TableParamDto.initPartitionParam(dateValue.toString());
        } else if (!Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_PARTITION.getValue())) {
            if (Objects.equals(type, TableTypeEnum.SINGLE_TABLE.getValue())) {
                return null;
            } else {
                dateValue = fieldMap.get(dateKey);
                Assert.isTrue(!Objects.isNull(dateValue), "年月表值获取失败,请检查配置或则时间字段是否有值");
                return TableParamDto.initDateParam(dateValue.toString());
            }
        } else {
            dateValue = fieldMap.get(dateKey);
            Object partitionValue = fieldMap.get(partitionKey);
            Assert.isTrue(!Objects.isNull(dateValue) && Objects.isNull(partitionValue), "年库分区表值获取失败,请检查配置或则分区字段是否有值");
            return TableParamDto.initYearPartitionParam(dateValue.toString(), partitionValue.toString());
        }
    }

    public String getFieldList() {
        if (CollectionUtils.isEmpty(this.fieldList)) {
            return "*";
        } else {
            List<String> filtered = this.fieldList.stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filtered)) {
                return "*";
            } else {
                String join = StringUtils.join(filtered.toArray(), ",");
                Assert.isTrue(PoUtil.isSqlInject(join), String.format("表字段存在sql注入,fields=%s", join));
                return join;
            }
        }
    }

    public static SelectModel initSelect(String dataBaseName, String tableName, List<String> fields, TableParamDto tableParamDto) {
        handleOtherInfo(dataBaseName, tableName, tableParamDto);
        return new SelectModel(dataBaseName, tableName, fields);
    }

    public static SelectModel initSelect(String dataBaseName, String tableName, List<String> fields) {
        return initSelect(dataBaseName, tableName, fields, null);
    }

    public static SelectModel initSelectByCode(String code, TableParamDto tableParamDto) {
        return initSelectByCode(code, null, tableParamDto);
    }

    public static SelectModel initSelectByCode(String code) {
        return initSelectByCode(code, null, null);
    }

    public static SelectModel initSelectByCode(String code, List<String> fields, TableParamDto tableParamDto) {
        TableInfoDto tableInfo = codeHandle(code, tableParamDto);
        return new SelectModel(tableInfo.getDataBaseName(), tableInfo.getName(), fields);
    }

    public static SelectModel initSelectByCode(String code, List<String> fields) {
        return initSelectByCode(code, fields, null);
    }

    public static InsertModel initInsert(String dataBaseName, String tableName, Map<String, Object> fieldMap) {
        return initInsert(dataBaseName, tableName, fieldMap, null);
    }

    public static InsertModel initInsert(String dataBaseName, String tableName, Map<String, Object> fieldMap, TableParamDto tableParamDto) {
        handleOtherInfo(dataBaseName, tableName, tableParamDto);
        Assert.isTrue(CollectionUtils.isNotEmpty(fieldMap), "字段信息不能为空");
        String join = StringUtils.join(fieldMap.keySet().toArray(), ",");
        Assert.isTrue(PoUtil.isSqlInject(join), String.format("表字段存在sql注入,fields=%s", join));
        return new InsertModel(dataBaseName, tableName, fieldMap, join);
    }

    public static InsertModel initInsertByCode(String code, Map<String, Object> fieldMap) {
        return initInsertByCode(code, fieldMap,  null);
    }

    public static InsertModel initInsertByCode(String code, Map<String, Object> fieldMap, TableParamDto tableParamDto) {
        TableInfoDto tableInfo = codeHandle(code, tableParamDto, fieldMap);
        return new InsertModel(tableInfo.getDataBaseName(), tableInfo.getName(), fieldMap, StringUtils.join(fieldMap.keySet().toArray(), ","));
    }

    public static UpdateModel initUpdate(String dataBaseName, String tableName, Map<String, Object> fieldMap) {
        return initUpdate(dataBaseName, tableName, fieldMap, null);
    }

    public static UpdateModel initUpdate(String dataBaseName, String tableName, Map<String, Object> fieldMap, TableParamDto tableParamDto) {
        handleOtherInfo(dataBaseName, tableName, tableParamDto);
        Assert.isTrue(CollectionUtils.isNotEmpty(fieldMap), "字段信息不能为空");
        String join = StringUtils.join(fieldMap.keySet().toArray(), ",");
        Assert.isTrue(PoUtil.isSqlInject(join), String.format("表字段存在sql注入,fields=%s", join));
        return new UpdateModel(dataBaseName, tableName, fieldMap);
    }

    public static UpdateModel initUpdateByCode(String code, Map<String, Object> fieldMap) {
        return initUpdateByCode(code, fieldMap, null);
    }

    public static UpdateModel initUpdateByCode(String code, Map<String, Object> fieldMap, TableParamDto tableParamDto) {
        TableInfoDto tableInfo = codeHandle(code, tableParamDto, fieldMap);
        return new UpdateModel(tableInfo.getDataBaseName(), tableInfo.getName(), fieldMap);
    }

    public static DeleteModel initDelete(String dataBaseName, String tableName) {
        handleOtherInfo(dataBaseName, tableName, null);
        return new DeleteModel(dataBaseName, tableName);
    }

    public static DeleteModel initDelete(String dataBaseName, String tableName, TableParamDto tableParamDto) {
        handleOtherInfo(dataBaseName, tableName, tableParamDto);
        return new DeleteModel(dataBaseName, tableName);
    }

    public static DeleteModel initDeleteByCode(String code) {
        TableInfoDto tableInfo = codeHandle(code, null);
        return new DeleteModel(tableInfo.getDataBaseName(), tableInfo.getName());
    }

    public static DeleteModel initDeleteByCode(String code, TableParamDto tableParamDto) {
        TableInfoDto tableInfo = codeHandle(code, tableParamDto);
        return new DeleteModel(tableInfo.getDataBaseName(), tableInfo.getName());
    }

    public static DeleteModel initDeleteByCode(String code, Map<String, Object> fieldMap) {
        TableInfoDto tableInfo = codeHandle(code, null, fieldMap);
        return new DeleteModel(tableInfo.getDataBaseName(), tableInfo.getName());
    }

    static {
        MAPPER = OrmUtilConfig.applicationContext.getBean(DynamicSqlMapper.class);
    }
}
