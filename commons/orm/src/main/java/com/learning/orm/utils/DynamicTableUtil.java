package com.learning.orm.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.io.grpc.internal.JsonUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.StringUtil;
import com.learning.orm.annotation.TableCode;
import com.learning.orm.annotation.UseStock;
import com.learning.orm.config.OrmUtilConfig;
import com.learning.orm.config.properties.OrmProperties;
import com.learning.orm.dto.*;
import com.learning.orm.enums.TableTypeEnum;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: DynamicTableUtil
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class DynamicTableUtil {
    private static final Logger log = LoggerFactory.getLogger(DynamicTableUtil.class);
    private static OrmProperties ormProperties;
    private static Environment environment;
    private static ConcurrentHashMap<String, List<Field>> filedMapCache = new ConcurrentHashMap();

    public DynamicTableUtil() {
    }

    @Autowired
    public void setOrmProperties(OrmProperties ormProperties) {
        DynamicTableUtil.ormProperties = ormProperties;
    }

    public static String getOriginalTableName(Class paramClazz) {
        Assert.isTrue(paramClazz.isAnnotationPresent(TableName.class), "动态表标识注解@TableCode不存在");
        TableName tn = (TableName)paramClazz.getAnnotation(TableName.class);
        return tn.value();
    }

    public static Class getPoGenericClass(String id) throws ClassNotFoundException {
        String mapperClazzStr = id.substring(0, id.lastIndexOf("."));
        Class mapperClazz = Class.forName(mapperClazzStr);
        Class basePo = null;
        Type[] interfacesTypes = mapperClazz.getGenericInterfaces();
        Type[] var5 = interfacesTypes;
        int var6 = interfacesTypes.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Type type = var5[var7];
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                basePo = (Class)pType.getActualTypeArguments()[0];
            }
        }

        return basePo;
    }

    public static void replaceSql(BoundSql boundSql, Map<String, String> map, SqlCommandType sqlCommandType) throws NoSuchFieldException, IllegalAccessException {
        String ormSql = boundSql.getSql();
        Iterator var4 = map.entrySet().iterator();

        String tenantValue;
        String where;
        while(var4.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var4.next();
            tenantValue = (String)entry.getKey();
            where = (String)entry.getValue();
            Assert.isTrue(PoUtil.isSqlInject(where), String.format("数据库名和表名存在sql注入,databaseTableName=%s", where));
            if (Objects.equals(sqlCommandType, SqlCommandType.SELECT)) {
                Boolean useNoLock = TableThreadLocalUtil.getUseWithNoLock();
                if (!Objects.isNull(useNoLock) && useNoLock) {
                    where = where + " with(nolock)";
                }
            }

            ormSql = replaceTable(ormSql, tenantValue, where);
            log.debug(Thread.currentThread().getName() + " , 将表名 {} 替换成 {} ", tenantValue, where);
        }

        String endSql = ormSql;
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        log.debug(Thread.currentThread().getName() + " 获取机构id进行拼接 :  !Objects.equals(sqlCommandType, SqlCommandType.INSERT) : " + !Objects.equals(sqlCommandType, SqlCommandType.INSERT) + " ， Objects.nonNull(ormProperties.getTenant()): " + Objects.nonNull(ormProperties.getTenant()) + " , ormProperties.getTenant(): " + ormProperties.getTenant());
        if (!Objects.equals(sqlCommandType, SqlCommandType.INSERT) && Objects.nonNull(ormProperties.getTenant()) && ormProperties.getTenant()) {
            tenantValue = TableThreadLocalUtil.getTenantValue();
            log.debug(Thread.currentThread().getName() + " 获取机构id进行拼接 :  tenantValue: " + tenantValue);
            if (StringUtil.isNotBlank(tenantValue)) {
                where = " where ";
                String lowerCaseOrmSql = ormSql.toLowerCase();
                log.debug("lowerCaseOrmSql： " + lowerCaseOrmSql);
                boolean skip = lowerCaseOrmSql.contains("select * from") && lowerCaseOrmSql.contains("tb_table") && lowerCaseOrmSql.contains("row_id =");
                if (!skip) {
                    endSql = "";
                    String[] sqlArr = ormSql.split(";sqlend;");
                    String[] var11 = sqlArr;
                    int var12 = sqlArr.length;

                    for(int var13 = 0; var13 < var12; ++var13) {
                        String everySql = var11[var13];
                        String rawEverySql = everySql;
                        everySql = everySql.toLowerCase();
                        String[] wheres = everySql.split("where");
                        String whereSql = "";

                        for(int i = 1; i < wheres.length; ++i) {
                            whereSql = whereSql + wheres[i];
                        }

                        if (!whereSql.toLowerCase().contains(ormProperties.getTenantId().toLowerCase())) {
                            String lastGroupStr = "";
                            String[] groups;
                            if (everySql.contains("order by")) {
                                groups = rawEverySql.split("(?i) order by");
                                rawEverySql = getString(groups);
                                lastGroupStr = " order by " + groups[groups.length - 1];
                            }

                            if (everySql.contains("having")) {
                                groups = rawEverySql.split("(?i) having");
                                rawEverySql = getString(groups);
                                lastGroupStr = " having " + groups[groups.length - 1];
                            }

                            if (everySql.contains("group by")) {
                                groups = rawEverySql.split("(?i) group by");
                                rawEverySql = getString(groups);
                                lastGroupStr = " group by " + groups[groups.length - 1];
                            }

                            if (everySql.contains(where)) {
                                rawEverySql = rawEverySql + " and " + ormProperties.getTenantId() + "='" + tenantValue + "' ";
                            } else {
                                rawEverySql = rawEverySql + " where " + ormProperties.getTenantId() + "='" + tenantValue + "' ";
                            }

                            rawEverySql = rawEverySql + lastGroupStr;
                        }

                        endSql = endSql + rawEverySql + ";";
                    }

                    if (endSql.endsWith(";")) {
                        endSql = endSql.substring(0, endSql.length() - 1);
                    }
                }
            }
        }

        if (endSql.contains(";sqlend;")) {
            endSql = endSql.replace(";sqlend;", ";");
        }

        field.set(boundSql, endSql);
    }

    private static String getString(String[] orders) {
        String prefixSql = "";

        for(int i = 0; i < orders.length - 1; ++i) {
            prefixSql = prefixSql + orders[i];
        }

        return prefixSql;
    }

    public static void handleGroupSql(BoundSql boundSql, GroupSqlListDto groupSqlListDto, SqlCommandType sqlCommandType) throws NoSuchFieldException, IllegalAccessException, JSQLParserException {
        String ormSql = boundSql.getSql();
        List<String> tables = groupSqlListDto.getTables();
        if (CollectionUtils.isNotEmpty(boundSql.getParameterMappings())) {
            List<ParameterMapping> initParameterMappings = new ArrayList(boundSql.getParameterMappings());
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

            for(int i = 1; i < tables.size(); ++i) {
                parameterMappings.addAll(initParameterMappings);
            }
        }

        if (Objects.equals(sqlCommandType, SqlCommandType.SELECT)) {
            ormSql = handleSelectSql(ormSql, tables);
        } else if (Objects.equals(sqlCommandType, SqlCommandType.UPDATE)) {
            ormSql = handleUpdate(ormSql, tables);
        } else if (Objects.equals(sqlCommandType, SqlCommandType.DELETE)) {
            ormSql = handleDelete(ormSql, tables);
        }

        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, ormSql);
    }

    public static String handleSelectSql(String ormSql, List<String> tables) {
        String sql = ormSql.toUpperCase();
        StringBuilder sb = new StringBuilder();
        String selectFrom = "";

        for(int i = 0; i < tables.size(); ++i) {
            String[] split;
            if (StringUtil.isBlank(selectFrom)) {
                split = sql.split(" FROM ");
                selectFrom = split[0] + " FROM ";
            }

            sb.append(selectFrom).append((String)tables.get(i));
            String[] split1;
            if (sql.contains(" GROUP BY ") && sql.contains(" WHERE ")) {
                split = sql.split(" GROUP BY ");
                split1 = split[0].split(" WHERE ");
                sb.append(" WHERE ").append(split1[1]);
            } else if (sql.contains(" ORDER BY ") && !sql.contains(" GROUP BY ") && sql.contains(" WHERE ")) {
                split = sql.split(" ORDER BY ");
                split1 = split[0].split(" WHERE ");
                sb.append(" WHERE ").append(split1[1]);
            } else if (sql.contains(" WHERE ")) {
                split = sql.split(" WHERE ");
                sb.append(" WHERE ").append(split[1]);
            }

            if (tables.size() - 1 != i) {
                sb.append(" UNION ALL ");
            }
        }

        String[] split;
        if (sql.contains(" GROUP BY ")) {
            split = sql.split(" GROUP BY ");
            sb.insert(0, "SELECT * FROM (").append(" ) T").append(" GROUP BY ").append(split[1]);
        } else if (sql.contains(" ORDER BY ")) {
            split = sql.split(" ORDER BY ");
            sb.insert(0, "SELECT * FROM (").append(" ) T").append(" ORDER BY ").append(split[1]);
        }

        return sb.toString();
    }

    public static String handleUpdate(String ormSql, List<String> tables) {
        String sql = ormSql.toUpperCase();
        StringBuilder sb = new StringBuilder();
        Iterator var4 = tables.iterator();

        while(var4.hasNext()) {
            String table = (String)var4.next();
            String[] split = sql.split(" SET ");
            sb.append("UPDATE ").append(table).append(" SET ").append(split[1]).append(";");
        }

        return sb.toString();
    }

    public static String handleDelete(String ormSql, List<String> tables) {
        String sql = ormSql.toUpperCase();
        StringBuilder sb = new StringBuilder();
        String selectFrom = "";

        for(Iterator var5 = tables.iterator(); var5.hasNext(); sb.append(";")) {
            String table = (String)var5.next();
            String[] split;
            if (StringUtil.isBlank(selectFrom)) {
                split = sql.split(" FROM ");
                selectFrom = split[0] + " FROM ";
            }

            sb.append(selectFrom).append(table);
            if (sql.contains(" WHERE ")) {
                split = sql.split(" WHERE ");
                sb.append(" WHERE ").append(split[1]);
            }
        }

        return sb.toString();
    }

    private static String replaceTable(String sql, String sourceTableName, String targetTableName) {
        return sql.replaceAll("(?<![A-Za-z0-9_.])" + sourceTableName + "(?![A-Za-z0-9_])", targetTableName);
    }

    public static void replaceUseStockSql(BoundSql boundSql, String useStockColumn) throws NoSuchFieldException, IllegalAccessException {
        String sql = boundSql.getSql();
        String oldStr = "(" + useStockColumn + ")(" + "=" + ")(\\?)";
        String newStr = "$1$2$1+$3";
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, sql.replaceAll(oldStr, newStr));
    }

    public static void initTableInfoByClass(Class cls) {
        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
        TableCode tableCode = (TableCode)cls.getAnnotation(TableCode.class);
        OrmUtilConfig.initTableInfoByTableCode(tableCode.value());
    }

    public static TableInfoDto checkTableInfo() {
        TableInfoDto table = TableThreadLocalUtil.getTableInfo();
        String name = table.getName();
        Assert.isTrue(StringUtil.isNotBlank(name), String.format("获取表名称失败,rowId=%s", table.getRowId()));
        return table;
    }

    public static void getDatabaseAndTableByKey(Class poGenericClass, Object paramObj, Map<String, String> map) {
        TableInfoDto table = checkTableInfo();
        TableParamDto tableParam = TableThreadLocalUtil.getTableParam();
        map.put(getOriginalTableName(poGenericClass), PoUtil.getTableName(tableParam, table));
        if (Objects.isNull(tableParam) && !Objects.isNull(paramObj)) {
            handleObjType(poGenericClass, paramObj, table, map);
        }

    }

    public static void getDatabaseAndTableByKey(Map<String, String> map) {
        TableInfoDto table = checkTableInfo();
        TableParamDto tableParam = TableThreadLocalUtil.getTableParam();
        if (Objects.nonNull(tableParam)) {
            map.put(TableThreadLocalUtil.getTableInfo().getName(), PoUtil.getTableName(tableParam, table));
        }

    }

    private static void handleObjType(Class poGenericClass, Object paramObj, TableInfoDto table, Map<String, String> map) {
        Class<?> aClass1 = paramObj.getClass();
        if (paramObj instanceof Map) {
            Map<String, Object> pMap = (Map)paramObj;
            Iterator var6 = pMap.entrySet().iterator();

            while(true) {
                while(true) {
                    Map.Entry entry;
                    Object item;
                    do {
                        if (!var6.hasNext()) {
                            return;
                        }

                        entry = (Map.Entry)var6.next();
                        item = entry.getValue();
                    } while(Objects.isNull(item));

                    Class<?> aClass = item.getClass();
                    if (aClass.isAnnotationPresent(TableCode.class) && aClass.isAnnotationPresent(TableName.class)) {
                        handleEntityObj(poGenericClass, item, table, map);
                    } else {
                        if (item instanceof MultiParamListDto) {
                            handleDiyObjectList(map, (MultiParamListDto)item);
                        }

                        if (item instanceof MultiParamDto) {
                            handleDiyObject(map, (MultiParamDto)item);
                        }

                        Iterator var11;
                        if (item instanceof Collection && Objects.equals(entry.getKey(), "COLLECTION_MULTI")) {
                            try {
                                Collection<String> collection = (Collection)item;
                                var11 = collection.iterator();

                                while(var11.hasNext()) {
                                    String o = (String)var11.next();
                                    OrmUtilConfig.initTableInfoByTableCode(o);
                                    TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
                                    map.put(tableInfo.getName(), getDatabaseAndTableName(tableInfo.getDataBaseName(), tableInfo.getName()));
                                }
                            } catch (ClassCastException var17) {
                                throw new ServiceException("COLLECTION_MULTI集合必须为字符串,请检查");
                            }
                        }

                        if (item instanceof Map && Objects.equals(entry.getKey(), "ORM_MAP_MULTI")) {
                            try {
                                Map<String, TableParamDto> map1 = (Map)item;
                                var11 = map1.entrySet().iterator();

                                while(var11.hasNext()) {
                                    Map.Entry<String, TableParamDto> entry1 = (Map.Entry)var11.next();
                                    String key = (String)entry1.getKey();
                                    TableParamDto value = (TableParamDto)entry1.getValue();
                                    OrmUtilConfig.initTableInfoByTableCode(key);
                                    TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
                                    map.put(tableInfo.getName(), PoUtil.getTableName(value, tableInfo));
                                }
                            } catch (ClassCastException var16) {
                                throw new ServiceException("ORM_MAP_MULTI集合key必须为字符串,value必须为TableParamDto对象");
                            }
                        }
                    }
                }
            }
        } else if (paramObj instanceof MultiParamListDto) {
            handleDiyObjectList(map, (MultiParamListDto)paramObj);
        } else if (paramObj instanceof MultiParamDto) {
            handleDiyObject(map, (MultiParamDto)paramObj);
        } else if (aClass1.isAnnotationPresent(TableCode.class) && aClass1.isAnnotationPresent(TableName.class)) {
            handleEntityObj(poGenericClass, paramObj, table, map);
        } else {
            map.put(getOriginalTableName(poGenericClass), getDatabaseAndTableName(table.getDataBaseName(), table.getName()));
        }

    }

    public static void handleEntityObj(Class poGenericClass, Object paramObj, TableInfoDto table, Map<String, String> map) {
        Class<?> aClass = paramObj.getClass();
        if (aClass.equals(poGenericClass)) {
            map.put(getOriginalTableName(poGenericClass), PoUtil.getTableName(handleEntityClass(poGenericClass, paramObj, table), table));
        } else {
            initTableInfoByClass(aClass);
            TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
            map.put(tableInfo.getName(), PoUtil.getTableName(handleEntityClass(aClass, paramObj, tableInfo), tableInfo));
        }

    }

    public static void handleDiyObjectList(Map<String, String> map, MultiParamListDto multiParamListDto) {
        List<MultiParamDto> listDtoList = multiParamListDto.getListDtoList();
        Iterator var3 = listDtoList.iterator();

        while(var3.hasNext()) {
            MultiParamDto dto = (MultiParamDto)var3.next();
            handleDiyObject(map, dto);
        }

    }

    public static void handleDiyObject(Map<String, String> map, MultiParamDto dto) {
        TableParamDto tableParamDto = dto.getTableParamDto();
        String tableCode = dto.getTableCode();
        OrmUtilConfig.initTableInfoByTableCode(tableCode);
        TableInfoDto tableInfo = TableThreadLocalUtil.getTableInfo();
        map.put(!StringUtils.isEmpty(dto.getReplaceCode()) ? dto.getReplaceCode() : tableInfo.getName(), Objects.isNull(tableParamDto) ? getDatabaseAndTableName(tableInfo.getDataBaseName(), tableInfo.getName()) : PoUtil.getTableName(tableParamDto, tableInfo));
    }

    public static String makeUseStockColumn(Object paramObj) {
        if (paramObj instanceof Map) {
            Map<String, Object> pMap = (Map)paramObj;
            Iterator var8 = pMap.entrySet().iterator();

            while(var8.hasNext()) {
                Map.Entry entry = (Map.Entry)var8.next();
                Object item = entry.getValue();
                if (!Objects.isNull(item)) {
                    Class<?> aClass = item.getClass();
                    String useStockColumnName = getUseStockColumnName(aClass);
                    if (StringUtil.isNotBlank(useStockColumnName)) {
                        return useStockColumnName;
                    }
                }
            }

            throw new ServiceException("没有从集合中获取到库存策略字段信息");
        } else {
            Class<?> aClass = paramObj.getClass();
            String useStockColumnName = getUseStockColumnName(aClass);
            if (StringUtil.isNotBlank(useStockColumnName)) {
                return useStockColumnName;
            } else {
                throw new ServiceException("没有从实体类中获取到库存策略字段信息");
            }
        }
    }

    public static String getUseStockColumnName(Class<?> aClass) {
        List<Field> fields = getAllField(aClass);
        Iterator var2 = fields.iterator();

        Field field;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            field = (Field)var2.next();
            field.setAccessible(true);
        } while(!field.isAnnotationPresent(UseStock.class));

        Assert.isTrue(field.isAnnotationPresent(TableField.class), "库存策略字段必须指定注解TableField");
        TableField tableField = (TableField)field.getAnnotation(TableField.class);
        return tableField.value();
    }

    public static TableParamDto handleEntityClass(Class poGenericClass, Object paramObj, TableInfoDto table) {
        Integer type = table.getIType();
        TableAnnotationDto fieldInfo = getFieldInfo(poGenericClass, paramObj, table);
        return getTableParamDtoByTableAnnotationDto(fieldInfo, type);
    }

    public static TableParamDto getTableParamDtoByTableAnnotationDto(TableAnnotationDto fieldInfo, Integer type) {
        String dateValue = fieldInfo.getDateValue();
        String partitionValue = fieldInfo.getPartitionValue();
        String wardValue = fieldInfo.getWardValue();
        if (Objects.equals(type, TableTypeEnum.WARD_TABLE.getValue())) {
            if (StringUtil.isNotBlank(wardValue)) {
                return TableParamDto.initWardParam(wardValue);
            }

            log.error("病区值为空");
        } else if (Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_PARTITION.getValue())) {
            if (StringUtil.isNotBlank(partitionValue)) {
                return TableParamDto.initPartitionParam(partitionValue);
            }

            log.error("分区值为空");
        } else if (Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_PARTITION.getValue())) {
            if (StringUtil.isNotBlank(dateValue) && StringUtil.isNotBlank(partitionValue)) {
                return TableParamDto.initYearPartitionParam(dateValue, partitionValue);
            }

            log.error("分区值和日期为空");
        } else {
            if (Objects.equals(type, TableTypeEnum.SINGLE_TABLE.getValue())) {
                return TableParamDto.initSingleTable();
            }

            if (Objects.equals(type, TableTypeEnum.SEVEN.getValue())) {
                if (StringUtil.isNotBlank(partitionValue)) {
                    return TableParamDto.initPartitionParam(partitionValue);
                }

                log.error("分区值为空");
            } else {
                if (StringUtil.isNotBlank(dateValue)) {
                    return TableParamDto.initDateParam(dateValue);
                }

                log.error("日期为空");
            }
        }

        return null;
    }

    public static String getPartitionTableName(String tableName, String partitionStr, Integer num) {
        StringBuilder tbName = new StringBuilder();
        String bm;
        if (!partitionStr.startsWith("w") && !partitionStr.startsWith("W")) {
            Assert.notNull(num, "缺少分区数量");
            if (num == -1) {
                bm = partitionStr;
            } else if (num <= 10) {
                bm = "0" + partitionStr.substring(partitionStr.length() - 1);
            } else {
                bm = partitionStr.substring(partitionStr.length() - 2);
            }
        } else {
            bm = "W";
        }

        tbName.append(tableName).append("_").append(bm);
        return tbName.toString();
    }

    private static TableAnnotationDto getFieldInfo(Class<?> c, Object paramObj, TableInfoDto table) {
        Map<String, Object> map = new HashMap(3);
        String dateKey = table.getDateKey();
        if (StringUtil.isNotBlank(dateKey)) {
            dateKey = dateKey.replaceAll("_", "");
        }

        String partitionKey = table.getPartitionKey();
        if (StringUtil.isNotBlank(partitionKey)) {
            partitionKey = partitionKey.replaceAll("_", "");
        }

        String wardKey = table.getWardKey();
        if (StringUtil.isNotBlank(wardKey)) {
            wardKey = wardKey.replaceAll("_", "");
        }

        List<Field> fields = getAllField(c);
        Iterator var8 = fields.iterator();

        while(var8.hasNext()) {
            Field field = (Field)var8.next();
            field.setAccessible(true);
            Object fieldValue = ReflectionUtils.getField(field, paramObj);
            if (field.getName().equalsIgnoreCase(dateKey)) {
                map.put("dateValue", fieldValue);
            }

            if (field.getName().equalsIgnoreCase(partitionKey)) {
                map.put("partitionValue", fieldValue);
            }

            if (field.getName().equalsIgnoreCase(wardKey)) {
                map.put("wardValue", fieldValue);
            }
        }

        return (TableAnnotationDto) JsonUtil.parseObject(JsonUtil.toJson(map), TableAnnotationDto.class);
    }

    public static String getDatabaseAndTableName(String databaseName, String tableName) {
        return StringUtil.isBlank(databaseName) ? tableName : String.format("%s..%s", databaseName, tableName);
    }

    public static void setTenantValue() {
        if (Objects.nonNull(ormProperties.getTenant()) && ormProperties.getTenant()) {
            try {
                if (environment == null) {
                    environment = (Environment)YhCloudSpringUtils.getBean(Environment.class);
                }

                String property = environment.getProperty("yanhua.mybatis.tokenTenantId");
                if (StringUtils.isEmpty(property)) {
                    property = "organizationId";
                }

                Map tokenMap = JwtTokenUtils.getTokenMap();
                Object tokenOrgCode = tokenMap.get(property);
                log.debug(Thread.currentThread().getName() + " 设置机构id到 ThreadLocal:  tenantValue: " + tokenOrgCode);
                if (Objects.nonNull(tokenOrgCode)) {
                    TableThreadLocalUtil.setTenantValue(String.valueOf(tokenOrgCode));
                } else {
                    log.warn("ORM从token中未获取到租户相关信息");
                }
            } catch (Exception var3) {
                if (StringUtils.isEmpty(TableThreadLocalUtil.getTenantValue())) {
                    log.error("ORM从token获取租户编号失败{}", var3.getMessage());
                } else {
                    log.error("ORM从ThreadLocal获取到tokenTenantId", var3.getMessage());
                }
            }
        }

    }

    public static List<Field> getAllField(Class clazz) {
        String name = clazz.getName();
        if (filedMapCache.containsKey(name)) {
            return (List)filedMapCache.get(name);
        } else {
            ArrayList fieldList;
            for(fieldList = new ArrayList(); clazz != null && !clazz.getSimpleName().equals("Object"); clazz = clazz.getSuperclass()) {
                Field[] declaredFields = clazz.getDeclaredFields();
                fieldList.addAll(Arrays.asList(declaredFields));
            }

            filedMapCache.put(name, fieldList);
            return fieldList;
        }
    }
}
