//package com.learning.orm.start;
//
//import cn.hutool.core.util.ClassUtil;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.learning.orm.annotation.Independent;
//import com.learning.orm.annotation.TableCode;
//import com.learning.orm.dto.TableInfoDto;
//import com.learning.orm.utils.PoUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.Iterator;
//import java.util.Set;
//
///**
// * @ClassName: YhCloudOrmScanAnnotation
// * @Description:
// * @Author: WangWei
// * @Date: 2024-05-24
// * @Version V1.0
// **/
//@Component
//public class YhCloudOrmScanAnnotation implements CommandLineRunner {
//    private static final Logger log = LoggerFactory.getLogger(YhCloudOrmScanAnnotation.class);
//
//    public YhCloudOrmScanAnnotation() {
//    }
//
//    public void run(String... args) throws Exception {
//        Set<Class<?>> scanPackage = ClassUtil.scanPackageByAnnotation("com.yanhua", TableCode.class);
//        Iterator var3 = scanPackage.iterator();
//
//        while(var3.hasNext()) {
//            Class<?> class1 = (Class)var3.next();
//            TableCode tableCodeAnnotation = (TableCode)class1.getAnnotation(TableCode.class);
//            TableName annotation = (TableName)class1.getAnnotation(TableName.class);
//            String value = tableCodeAnnotation.value();
//            if (StringUtils.isEmpty(value)) {
//                throw new RuntimeException("@TableCode 不允许空值, " + class1.getName());
//            }
//
//            if (annotation != null) {
//                PoUtil.TABLE_NAME_CACHE.put(value, annotation.value());
//            }
//
//            if (value.startsWith("FIXED:")) {
//                Independent independentAnnotation = (Independent)class1.getAnnotation(Independent.class);
//                if (independentAnnotation == null) {
//                    throw new RuntimeException("当@TableCode 以FIXEDCODE 开头时，必须配置 @com.yanhua.cloud.orm.annotation.Independent 注解, " + class1.getName());
//                }
//
//                TableInfoDto tableInfoDto = translate2TableInfoDto(independentAnnotation, tableCodeAnnotation.value());
//                PoUtil.TABLE_CACHE_INFO.put(tableCodeAnnotation.value(), tableInfoDto);
//            }
//        }
//
//        log.info("yh-orm 初始化完成", YhCloudOrmScanAnnotation.class.getPackage().getImplementationVersion());
//    }
//
//    static TableInfoDto translate2TableInfoDto(Independent independent, String tableCode) {
//        TableInfoDto tableInfoDto = new TableInfoDto();
//        tableInfoDto.setRowId(tableCode);
//        tableInfoDto.setDataBaseName(independent.databseName());
//        tableInfoDto.setDateKey(independent.dateKey());
//        tableInfoDto.setIType(independent.type().getValue());
//        tableInfoDto.setName(independent.tableName());
//        tableInfoDto.setPartitionKey(independent.partitionKey());
//        tableInfoDto.setPartitionNum(independent.partitionNum());
//        tableInfoDto.setPrimaryKey(independent.primaryKey());
//        tableInfoDto.setWardKey(independent.wardKey());
//        return tableInfoDto;
//    }
//}
