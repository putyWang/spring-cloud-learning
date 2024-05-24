package com.learning.orm.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: DynamicTableConst
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public interface DynamicTableConst {
    String SQL_SUFFIX = ";sqlend;";
    String FIXEDCODE = "FIXED:";
    String UPDATE = "update";
    String QUERY = "query";
    String STR1 = "-";
    String STR2 = "/";
    String STR3 = "|";
    String STR4 = "_";
    String DATABASE_NAME_AND_TABLE_NAME = "%s..%s";
    String SMALL_W = "w";
    String BIG_W = "W";
    String RELATIVE_PATH = "http://%s%s%s";
    Map<String, String> HANDLE_TYPE_MAP = new HashMap<String, String>() {
        {
            this.put("java.util.Date", ",jdbcType=TIMESTAMP");
            this.put("java.lang.Float", ",jdbcType=FLOAT");
            this.put("java.lang.Double", ",jdbcType=DOUBLE");
            this.put("java.lang.Boolean", ",jdbcType=BOOLEAN");
        }
    };
    String ROW_ID = "row_id";
    String TABLE_NAME = "name";
    String DATABASE_NAME = "database_name";
    String I_TYPE = "iType";
    String SQL_LIKE = " like ";
    String SQL_LIKE1 = " like";
}
