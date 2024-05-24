package com.learning.orm.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: OrmProperties
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@ConfigurationProperties(
        prefix = "learning.mybatis"
)
@Component
@Setter
@Getter
public class OrmProperties {
    private String applicationName = "data-standards-manage";
    private String contextPath = "/dataStandardsManage";
    private String ormApi = "/table/v2/getTableInfo?rowId={tableCode}";
    private Boolean ormSql = false;
    private String ormTableName = "data_standard..tb_table";
    private Boolean tenant = false;
    private String tenantId = "CJGID";
    public static boolean printSql;

    @Value("${yanhua.mybatis.printSql:false}")
    public static void setPrintSql(boolean printSql) {
        OrmProperties.printSql = printSql;
    }
}
