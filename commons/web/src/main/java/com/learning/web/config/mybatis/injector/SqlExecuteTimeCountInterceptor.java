package com.learning.web.config.mybatis.injector;


import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * Sql执行时间记录拦截器
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@Component
@Log4j2
public class SqlExecuteTimeCountInterceptor implements Interceptor {

    /**
     * 记录的最大SQL长度
     */
    private final static int MAX_SQL_LENGTH = 200;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) target;
        try {
            return invocation.proceed();
        } finally {
            // 格式化Sql语句，去除换行符，替换参数
            BoundSql boundSql = statementHandler.getBoundSql();
            log.info("执行 SQL：[ , {} ]执行耗时[ {} ms]", formatSQL(boundSql.getSql(), boundSql.getParameterObject(), boundSql.getParameterMappings()), System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) { }

    /**
     * 格式化/美化 SQL语句
     *
     * @param sql                  sql 语句
     * @param parameterObject      参数的Map
     * @param parameterMappingList 参数的List
     * @return 格式化之后的SQL
     */
    private String formatSQL(String sql, Object parameterObject, List<ParameterMapping> parameterMappingList) {
        // 1.输入sql字符串空判断
        if (sql == null || sql.length() == 0) {
            return "";
        }
        // 2.美化sql
        sql = beautifySql(sql);
        // 3.不传参数的场景，直接把sql美化一下返回出去
        if (parameterObject == null || parameterMappingList == null || parameterMappingList.size() == 0) {
            return sql;
        }
        return LimitSQLLength(sql);
    }


    /**
     * 返回限制长度之后的SQL语句
     * @param sql 原始SQL语句
     */
    private String LimitSQLLength(String sql) {
        if (sql == null || sql.length() == 0) {
            return "";
        }
        if (sql.length() > MAX_SQL_LENGTH) {
            return sql.substring(0, MAX_SQL_LENGTH);
        } else {
            return sql;
        }
    }

    /**
     * 美化sql
     * @param sql sql语句
     */
    private String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", "  ");
        return sql;
    }
}
