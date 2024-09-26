//package com.learning.orm.utils;
//
//import com.learning.core.utils.StringUtil;
//import com.learning.orm.annotation.TableCode;
//import com.learning.orm.config.OrmUtilConfig;
//import com.learning.orm.dto.TableInfoDto;
//import com.learning.orm.dto.TableParamDto;
//import com.learning.orm.dto.TableYearPartitionDto;
//import com.learning.orm.enums.TableTypeEnum;
//import lombok.extern.log4j.Log4j2;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.regex.Pattern;
//
///**
// * @ClassName: PoUtil
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//@Log4j2
//public class PoUtil {
//    public static ConcurrentHashMap<String, TableInfoDto> TABLE_CACHE_INFO = new ConcurrentHashMap<>();
//    public static ConcurrentHashMap<String, String> TABLE_NAME_CACHE = new ConcurrentHashMap<>();
//    private static final String REQ = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|union|and|or|delete|insert|truncate|char|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
//    private static final Pattern SQL_PATTERN;
//
//    private PoUtil() {
//    }
//
//    /**
//     *
//     * @param cls
//     * @param minTime
//     * @param maxTime
//     * @return
//     */
//    public static List<String> getTimeSlotTableByClass(Class<?> cls, String minTime, String maxTime) {
//        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
//        return getTimeSlotTableByTableCode(cls.getAnnotation(TableCode.class).value(), minTime, maxTime);
//    }
//
//    public static List<String> getTimeSlotTableByClass(Class<?> cls, String minTime, String maxTime, TableTypeEnum typeEnum) {
//        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
//        return getTimeSlotTableByTableCode(cls.getAnnotation(TableCode.class).value(), minTime, maxTime, typeEnum);
//    }
//
//    public static List<String> getTimeSlotTableByTableCode(String tableCode, String minTime, String maxTime) {
//        return getTimeSlotTableByTableCode(tableCode, minTime, maxTime, null);
//    }
//
//
//    public static List<String> getTimeSlotTableByTableCode(String tableCode, String minTime, String maxTime, TableTypeEnum typeEnum) {
//        List<String> list = new ArrayList<>();
//        OrmUtilConfig.initTableInfoByTableCode(tableCode);
//        TableInfoDto table = TableThreadLocalUtil.getTableInfo();
//        LocalDate minDate = handleTimeStr(minTime);
//        LocalDate maxDate = handleTimeStr(maxTime);
//        String dataBaseName = table.getDataBaseName();
//        String tableName = table.getName();
//        Integer type = Objects.isNull(typeEnum) ? table.getIType() : typeEnum.getValue();
//        Assert.isTrue(StringUtil.isNotBlank(dataBaseName), "数据库名称不存在");
//        Assert.isTrue(StringUtil.isNotBlank(tableName), "表名称不存在");
//        Assert.isTrue(Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_YEAR_TABLE.getValue()) || Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_MONTH_TABLE.getValue()) || Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_YEAR_TABLE.getValue()) || Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_MONTH_TABLE.getValue()), String.format("不是时间分区表,表类型iType =%s", type));
//        StringBuilder sb = new StringBuilder(dataBaseName);
//        int year = 0;
//
//        while(true) {
//            while(minDate.compareTo(maxDate) <= 0) {
//                String str;
//                if (!Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_YEAR_TABLE.getValue()) && !Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_MONTH_TABLE.getValue())) {
//                    str = sb.toString();
//                } else {
//                    str = sb.toString() + minDate.getYear();
//                }
//
//                if (!Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_YEAR_TABLE.getValue()) && !Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_YEAR_TABLE.getValue())) {
//                    int monthValue = minDate.getMonthValue();
//                    if (monthValue < 10) {
//                        str = DynamicTableUtil.getDatabaseAndTableName(str, tableName + minDate.getYear() + "0" + monthValue);
//                    } else {
//                        str = DynamicTableUtil.getDatabaseAndTableName(str, tableName + minDate.getYear() + monthValue);
//                    }
//                } else {
//                    if (minDate.getYear() == year) {
//                        minDate = minDate.plusMonths(1L);
//                        continue;
//                    }
//
//                    year = minDate.getYear();
//                    str = DynamicTableUtil.getDatabaseAndTableName(str, tableName + minDate.getYear());
//                }
//
//                list.add(str);
//                minDate = minDate.plusMonths(1L);
//            }
//
//            return list;
//        }
//    }
//
//    public static String getDatabaseAndTable(Class cls, TableParamDto tableParamDto) {
//        Assert.isTrue(cls.isAnnotationPresent(TableCode.class), "动态表标识注解@TableCode不存在");
//        TableCode tableCode = (TableCode)cls.getAnnotation(TableCode.class);
//        return getDatabaseAndTable(tableCode.value(), tableParamDto);
//    }
//
//    public static String getDatabaseAndTable(String tableCode, TableParamDto tableParamDto) {
//        OrmUtilConfig.initTableInfoByTableCode(tableCode);
//        TableInfoDto table = DynamicTableUtil.checkTableInfo();
//        return Objects.isNull(tableParamDto) ? DynamicTableUtil.getDatabaseAndTableName(table.getDataBaseName(), table.getName()) : getTableName(tableParamDto, table);
//    }
//
//    static String getTableName(TableParamDto tableParam, TableInfoDto table) {
//        Integer type = table.getIType();
//        String dataBaseName = table.getDataBaseName();
//        String name = table.getName();
//        if (table.getName().contains("..tb_table")) {
//            return table.getName();
//        } else if (Objects.isNull(tableParam)) {
//            return DynamicTableUtil.getDatabaseAndTableName(dataBaseName, name);
//        } else {
//            String tableField = tableParam.getTableField();
//            TableYearPartitionDto yearPartition = tableParam.getYearPartitionDto();
//            TableTypeEnum typeEnum = tableParam.getTypeEnum();
//            if (!Objects.isNull(typeEnum)) {
//                type = typeEnum.getValue();
//            }
//
//            if (Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_PARTITION.getValue())) {
//                Assert.notNull(yearPartition, "年库分区->分区对象不能为空");
//                String dateKey = yearPartition.getDateKey();
//                String partitionKey = yearPartition.getPartitionKey();
//                Assert.isTrue(StringUtil.isNotBlank(dateKey), "年库分区->时间key值不能为空");
//                Assert.isTrue(StringUtil.isNotBlank(partitionKey), "年库分区->分区key值不能为空");
//                LocalDate minDate = handleTimeStr(dateKey);
//                Assert.isTrue(StringUtil.isNotBlank(dataBaseName), "年库分区->数据库名称不能为空");
//                dataBaseName = dataBaseName + minDate.getYear();
//                Integer num = Objects.isNull(yearPartition.getPartitionNum()) ? table.getPartitionNum() : yearPartition.getPartitionNum();
//                name = DynamicTableUtil.getPartitionTableName(name, partitionKey, num);
//                return DynamicTableUtil.getDatabaseAndTableName(dataBaseName, name);
//            } else if (!Objects.isNull(type) && type <= TableTypeEnum.WARD_TABLE.getValue() && type >= TableTypeEnum.SINGLE_TABLE.getValue() && (tableParam == null || tableParam.getTypeEnum() == null || tableParam.getTypeEnum().getValue() != 0)) {
//                Assert.isTrue(StringUtil.isNotBlank(tableField), "分区值获取失败");
//                LocalDate localDate;
//                if (Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_YEAR_TABLE.getValue()) || Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_MONTH_TABLE.getValue())) {
//                    localDate = handleTimeStr(tableField);
//                    Assert.isTrue(StringUtil.isNotBlank(dataBaseName), "年库处理->数据库名称不能为空");
//                    dataBaseName = dataBaseName + localDate.getYear();
//                }
//
//                if (Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_YEAR_TABLE.getValue()) || Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_YEAR_TABLE.getValue())) {
//                    localDate = handleTimeStr(tableField);
//                    name = name + localDate.getYear();
//                }
//
//                if (Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_MONTH_TABLE.getValue()) || Objects.equals(type, TableTypeEnum.YEAR_LIBRARY_MONTH_TABLE.getValue())) {
//                    localDate = handleTimeStr(tableField);
//                    int monthValue = localDate.getMonthValue();
//                    if (monthValue < 10) {
//                        name = name + localDate.getYear() + "0" + monthValue;
//                    } else {
//                        name = name + localDate.getYear() + monthValue;
//                    }
//                }
//
//                if (Objects.equals(type, TableTypeEnum.SINGLE_LIBRARY_PARTITION.getValue())) {
//                    Integer num = !Objects.isNull(yearPartition) && !Objects.isNull(yearPartition.getPartitionNum()) ? yearPartition.getPartitionNum() : table.getPartitionNum();
//                    name = DynamicTableUtil.getPartitionTableName(name, tableField, num);
//                }
//
//                if (Objects.equals(type, TableTypeEnum.WARD_TABLE.getValue())) {
//                    name = name + tableField;
//                }
//
//                if (Objects.equals(type, TableTypeEnum.SEVEN.getValue())) {
//                    name = name + "_" + tableField;
//                }
//
//                return DynamicTableUtil.getDatabaseAndTableName(dataBaseName, name);
//            } else {
//                log.debug("没有处理的分表类型或指定为单表,返回单表");
//                return DynamicTableUtil.getDatabaseAndTableName(dataBaseName, name);
//            }
//        }
//    }
//
//    static LocalDate handleTimeStr(String timeStr) {
//        Assert.isTrue(StringUtil.isNotBlank(timeStr), "时间字段不能为空");
//        String time = timeStr.replaceAll("-", "").replaceAll("/", "");
//        Assert.isTrue(StringUtil.isNotBlank(time), "时间字段无效");
//        if (time.length() >= 8) {
//            time = time.substring(0, 6) + "01";
//        } else if (time.length() == 4) {
//            time = time + "0101";
//        } else if (time.length() == 6) {
//            time = time + "01";
//        }
//
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
//        return LocalDate.parse(time, df);
//    }
//
//    public static boolean isSqlInject(String str) {
//        return ! SQL_PATTERN.matcher(str).find();
//    }
//
//    static {
//        SQL_PATTERN = Pattern.compile(REQ, 2);
//    }
//}
