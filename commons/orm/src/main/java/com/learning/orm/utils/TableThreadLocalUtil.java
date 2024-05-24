package com.learning.orm.utils;

import com.alibaba.fastjson.JSON;
import com.learning.orm.dto.TableInfoDto;
import com.learning.orm.dto.TableParamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: TableThreadLocalUtil
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
public class TableThreadLocalUtil {
    private static final Logger log = LoggerFactory.getLogger(TableThreadLocalUtil.class);
    private static ThreadLocal<TableInfoDto> TABLE_INFO = new ThreadLocal();
    private static ThreadLocal<TableParamDto> TABLE_PARAM = new ThreadLocal();
    private static ThreadLocal<Boolean> USE_STOCK = new ThreadLocal();
    private static ThreadLocal<Boolean> USE_WITH_NO_LOCK = new ThreadLocal();
    public static ThreadLocal<String> TENANT_VALUE = new ThreadLocal();

    private TableThreadLocalUtil() {
    }

    public static void setTableInfo(TableInfoDto dto) {
        log.debug(Thread.currentThread().getName() + "  setTableInfo : " + JSON.toJSONString(dto));
        TABLE_INFO.set(dto);
    }

    public static TableInfoDto getTableInfo() {
        TableInfoDto tableInfoDto = TABLE_INFO.get();
        log.debug(Thread.currentThread().getName() + "  getTableInfo : " + JSON.toJSONString(tableInfoDto));
        return tableInfoDto;
    }

    public static void setTableParam(TableParamDto dto) {
        printstack();
        log.debug(Thread.currentThread().getName() + "  setTableParam : " + JSON.toJSONString(dto));
        TABLE_PARAM.set(dto);
    }

    public static TableParamDto getTableParam() {
        TableParamDto tableParamDto = TABLE_PARAM.get();
        log.debug(Thread.currentThread().getName() + "  getTableParam : " + JSON.toJSONString(tableParamDto));
        return tableParamDto;
    }

    public static Boolean getUseStock() {
        return (Boolean)USE_STOCK.get();
    }

    public static void setUseStock(Boolean useStock) {
        USE_STOCK.set(useStock);
    }

    public static Boolean getUseWithNoLock() {
        return (Boolean)USE_WITH_NO_LOCK.get();
    }

    public static void setUseWithNoLock(Boolean useNoLock) {
        USE_WITH_NO_LOCK.set(useNoLock);
    }

    public static String getTenantValue() {
        return (String)TENANT_VALUE.get();
    }

    public static void setTenantValue(String tenant) {
        TENANT_VALUE.set(tenant);
    }

    public static void remove() {
        printstack();
        log.debug(Thread.currentThread().getName() + " remove:  TABLE_PARAM : " + JSON.toJSONString(TABLE_PARAM.get()) + " , TABLE_INFO: " + JSON.toJSONString(TABLE_INFO.get()));
        TABLE_INFO.remove();
        TABLE_PARAM.remove();
        USE_STOCK.remove();
        USE_WITH_NO_LOCK.remove();
        TENANT_VALUE.remove();
    }

    private static void printstack() {
    }
}
