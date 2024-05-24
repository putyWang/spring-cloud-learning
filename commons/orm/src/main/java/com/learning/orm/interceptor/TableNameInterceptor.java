package com.learning.orm.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.io.grpc.internal.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.learning.orm.annotation.TableCode;
import com.learning.orm.config.properties.OrmProperties;
import com.learning.orm.dto.GroupSqlListDto;
import com.learning.orm.utils.DynamicTableUtil;
import com.learning.orm.utils.TableThreadLocalUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;

/**
 * @ClassName: TableNameInterceptor
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
@Log4j2
public class TableNameInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement)args[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameterObject = args[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String id = mappedStatement.getId();
        String mapperClazzStr = id.substring(0, id.lastIndexOf("."));
        Class mapperClazz = Class.forName(mapperClazzStr);
        Object paramObj = boundSql.getParameterObject();
        if (paramObj instanceof Map) {
            Map<String, Object> pMap = (Map)paramObj;
            Iterator var19 = pMap.entrySet().iterator();

            while(var19.hasNext()) {
                Map.Entry entry = (Map.Entry)var19.next();
                Object item = entry.getValue();
                if (!Objects.isNull(item) && item instanceof GroupSqlListDto) {
                    DynamicTableUtil.handleGroupSql(boundSql, (GroupSqlListDto)item, sqlCommandType);
                    break;
                }
            }

            Map<String, String> map = new HashMap();
            if (Objects.equals(mapperClazz, YhDynamicSqlMapper.class)) {
                DynamicTableUtil.getDatabaseAndTableByKey(map);
            } else {
                Class poGenericClass = DynamicTableUtil.getPoGenericClass(id);
                if (Objects.isNull(poGenericClass) || !poGenericClass.isAnnotationPresent(TableCode.class)) {
                    MappedStatement newMappedStatement = this.newMappedStatement(mappedStatement, new TableNameInterceptor.BoundSqlSource(boundSql));
                    MetaObject metaObject = MetaObject.forObject(newMappedStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
                    args[0] = newMappedStatement;
                    metaObject.setValue("sqlSource.boundSql.sql", modifyLikeSql(boundSql.getSql(), parameterObject, boundSql));
                    return this.printSql(invocation);
                }

                DynamicTableUtil.initTableInfoByClass(poGenericClass);
                DynamicTableUtil.getDatabaseAndTableByKey(poGenericClass, paramObj, map);
            }

            if (CollectionUtil.isNotEmpty(map)) {
                DynamicTableUtil.replaceSql(boundSql, map, sqlCommandType);
            }
        } else if (paramObj instanceof GroupSqlListDto) {
            DynamicTableUtil.handleGroupSql(boundSql, (GroupSqlListDto)paramObj, sqlCommandType);
        } else {
            Map<String, String> map = new HashMap();
            if (Objects.equals(mapperClazz, YhDynamicSqlMapper.class)) {
                DynamicTableUtil.getDatabaseAndTableByKey(map);
            } else {
                Class poGenericClass = DynamicTableUtil.getPoGenericClass(id);
                if (Objects.isNull(poGenericClass) || !poGenericClass.isAnnotationPresent(TableCode.class)) {
                    MappedStatement newMappedStatement = this.newMappedStatement(mappedStatement, new TableNameInterceptor.BoundSqlSource(boundSql));
                    MetaObject metaObject = MetaObject.forObject(newMappedStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
                    args[0] = newMappedStatement;
                    metaObject.setValue("sqlSource.boundSql.sql", modifyLikeSql(boundSql.getSql(), parameterObject, boundSql));
                    return this.printSql(invocation);
                }

                DynamicTableUtil.initTableInfoByClass(poGenericClass);
                DynamicTableUtil.getDatabaseAndTableByKey(poGenericClass, paramObj, map);
            }

            if (CollectionUtil.isNotEmpty(map)) {
                DynamicTableUtil.replaceSql(boundSql, map, sqlCommandType);
            }

            Boolean useStock = TableThreadLocalUtil.getUseStock();
            if (Objects.nonNull(useStock) && useStock && Objects.equals(sqlCommandType, SqlCommandType.UPDATE)) {
                DynamicTableUtil.replaceUseStockSql(boundSql, DynamicTableUtil.makeUseStockColumn(boundSql.getParameterObject()));
            }
        }

        MappedStatement newMappedStatement = this.newMappedStatement(mappedStatement, new TableNameInterceptor.BoundSqlSource(boundSql));
        MetaObject metaObject = MetaObject.forObject(newMappedStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        args[0] = newMappedStatement;
        metaObject.setValue("sqlSource.boundSql.sql", modifyLikeSql(boundSql.getSql(), parameterObject, boundSql));
        return this.printSql(invocation);
    }

    public Object printSql(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        boolean normal = false;
        Instant start = Instant.now();

        Object var5;
        try {
            Object proceed = invocation.proceed();
            normal = true;
            var5 = proceed;
        } finally {
            if (OrmProperties.printSql) {
                FormatSqlUtils.getSqlJson(start, invocation, normal);
            }

        }

        return var5;
    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            String[] var5 = ms.getKeyProperties();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String keyProperty = var5[var7];
                keyProperties.append(keyProperty).append(".");
            }

            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private static String modifyLikeSql(String sql, Object parameterObject, BoundSql boundSql) {
        if (!(parameterObject instanceof HashMap)) {
            if (sql.contains(";sqlend;")) {
                sql = sql.replace(";sqlend;", " ; ");
            }

            return sql;
        } else if (sql.toLowerCase().contains(" like ") && sql.toLowerCase().contains("?")) {
            String[] strList = sql.split("\\?");
            Set<String> keyNames = new HashSet();

            String keyName;
            for(int i = 0; i < strList.length; ++i) {
                keyName = strList[i].toLowerCase().trim();
                if (keyName.endsWith(" like")) {
                    String keyName = ((ParameterMapping)boundSql.getParameterMappings().get(i)).getProperty();
                    keyNames.add(keyName);
                }
            }

            Iterator var12 = keyNames.iterator();

            while(true) {
                while(true) {
                    Object param;
                    HashMap parameter;
                    String mapKey;
                    label78:
                    do {
                        while(var12.hasNext()) {
                            keyName = (String)var12.next();
                            parameter = (HashMap)parameterObject;
                            if (sql.toLowerCase().contains(" like ?")) {
                                mapKey = keyName;
                                if (keyName.contains("ew.paramNameValuePairs.")) {
                                    AbstractWrapper wrapper = (AbstractWrapper)parameter.get("ew");
                                    parameter = (HashMap)wrapper.getParamNameValuePairs();
                                    String[] keyList = keyName.split("\\.");
                                    mapKey = keyList[2];
                                }

                                param = parameter.get(mapKey);
                                continue label78;
                            }

                            String[] split = keyName.split("\\.");
                            param = new HashMap(parameter);

                            for(int i = 0; i < split.length; ++i) {
                                if (i + 1 == split.length) {
                                    Object param = ((Map)param).get(split[i]);
                                    if (existLikeSpecialCharacters(param)) {
                                        if (i > 0) {
                                            ((Map)param).put(split[i], escapeChar(param.toString()));
                                            parameter.put(split[0], param);
                                        } else {
                                            parameter.put(keyName, escapeChar(param.toString()));
                                        }
                                    }
                                } else {
                                    param = (Map) JsonUtil.parseObject(JsonUtil.toJson(((Map)param).get(split[i])), Map.class);
                                }
                            }
                        }

                        if (boundSql.getSql().contains(";sqlend;")) {
                            sql = boundSql.getSql().replace(";sqlend;", " ; ");
                        }

                        return sql;
                    } while(!existLikeSpecialCharacters(param));

                    if (param.toString().startsWith("%") && param.toString().endsWith("%")) {
                        parameter.put(mapKey, String.format("%%%s%%", escapeChar(param.toString().substring(1, param.toString().length() - 1))));
                    } else if (param.toString().startsWith("%") && !param.toString().endsWith("%")) {
                        parameter.put(mapKey, String.format("%%%s", escapeChar(param.toString().substring(1))));
                    } else if (!param.toString().startsWith("%") && param.toString().endsWith("%")) {
                        parameter.put(mapKey, String.format("%s%%", escapeChar(param.toString().substring(0, param.toString().length() - 1))));
                    }
                }
            }
        } else {
            if (sql.contains(";sqlend;")) {
                sql = sql.replace(";sqlend;", " ; ");
            }

            return sql;
        }
    }

    public static boolean existLikeSpecialCharacters(Object param) {
        return param instanceof String && (param.toString().contains("_") || param.toString().contains("%"));
    }

    public static String escapeChar(String str) {
        return str.replace("_", "[_]").replace("%", "[%]").replace("'", "''");
    }

    @AllArgsConstructor
    class BoundSqlSource implements SqlSource {
        private BoundSql boundSql;

        @Override
        public BoundSql getBoundSql(Object object) {
            return this.boundSql;
        }
    }
}
