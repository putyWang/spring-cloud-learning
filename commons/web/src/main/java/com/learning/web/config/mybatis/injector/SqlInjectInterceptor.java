package com.learning.web.config.mybatis.injector;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.xmltags.ChooseSqlNode;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 防止sql脚本注入，拦截占位符${}参数值，可拦截CRUD，默认用于查询
 *
 * @author yove
 *
 */
@Component
@Intercepts({
        @Signature(
                type = Executor.class, method = "query",
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class
                }
        )
})
@Log4j2
public class SqlInjectInterceptor implements Interceptor {

    private static Map<String, Object> cache = new ConcurrentHashMap<>();

    public static String[] KEYWORDS = ";|'|\"|\\|--|_|/*|%|#|//|/+|=|or|and|like|select|insert|update|delete|alert|drop|truncate|declare|exec|execute|create|xp_|sp_|0x"
            .split("\\|");

    /**
     * 配置sql保留关键字
     *
     * @param keywords
     */
    @Value("${sql.keywords:}")
    public void setKeywords(String keywords) {
        if (StringUtils.isNotEmpty(keywords)) {
            SqlInjectInterceptor.KEYWORDS = keywords.split("\\|");
        }
    }

    @Override
    public Object plugin(Object object) {
        if (object instanceof Executor) {
            return Plugin.wrap(object, this);
        }
        return object;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameterObject = invocation.getArgs()[1];
        if (null != parameterObject) {
            MetaObject mappedStatement = SystemMetaObject.forObject(invocation.getArgs()[0]);
            if (mappedStatement.getValue("sqlSource") instanceof DynamicSqlSource) {
                Set<String> parameterKeys = (Set<String>) cache.get(mappedStatement.getValue("id"));
                if (null == parameterKeys) {
                    SqlNode rootSqlNode = (SqlNode) mappedStatement.getValue("sqlSource.rootSqlNode");
                    parameterKeys = parseSqlNode(rootSqlNode);
                    cache.put((String) mappedStatement.getValue("id"), parameterKeys);
                }
                if (null != parameterKeys && !parameterKeys.isEmpty()) {
                    MetaObject parameterMo = SystemMetaObject.forObject(parameterObject);
                    for (String parameterKey : parameterKeys) {
                        if (parameterMo.hasGetter(parameterKey) || parameterObject instanceof Map) {
                            Object value = parameterMo.getValue(parameterKey);
                            if (null != value && !"".equals(value)) {
                                parameterMo.setValue(parameterKey, process(value));
                            }
                        }
                    }
                }
            }
        }
        return invocation.proceed();
    }

    /**
     * 替换占位符${}参数值中的sql保留关键字为N/A
     *
     * @param value
     * @return
     */
    private static Object process(Object value) {
        for (int i = 0; i < KEYWORDS.length; i++) {
            String str = KEYWORDS[i];
            if (i > 11) {
                if (Pattern.compile("\\s+").matcher(value.toString()).find()) {
                    for (String s : value.toString().split("\\s+")) {
                        if (StringUtils.equalsIgnoreCase(s, str)) {
                            value = value.toString().replaceAll(str, "N/A");
                            log.warn("sql脚本中特殊字符【{}】已被过滤", str);
                        }
                    }
                }
            } else if (value.toString().toLowerCase().contains(str)) {
                value = value.toString().replaceAll(Pattern.quote(str), "N/A");
                log.warn("sql脚本中特殊字符【{}】已被过滤", str);
            }
        }
        return value;
    }

    /**
     * 解析sqlNode中占位符${}的参数key
     *
     * @param sqlNode
     * @return
     * @throws Exception
     */
    private static Set<String> parseSqlNode(SqlNode sqlNode) throws Exception {
        Set<String> parameterKeys = new HashSet<>();
        if (sqlNode instanceof TextSqlNode) {
            Field textField = (Field) cache.get("text");
            if (null == textField) {
                textField = ReflectionUtils.findField(sqlNode.getClass(), "text");
                textField.setAccessible(true);
                cache.put("text", textField);
            }
            String text = (String) textField.get(sqlNode);
            String[] texts = StringUtils.substringsBetween(text, "${", "}");
            parameterKeys.addAll(Arrays.asList(texts));
            return parameterKeys;
        }

        Field contentsField = null, defaultSqlNodeField = null, ifSqlNodesField = null;
        if (sqlNode instanceof ChooseSqlNode) {
            defaultSqlNodeField = (Field) cache.get("defaultSqlNode");
            if (null == defaultSqlNodeField) {
                defaultSqlNodeField = ReflectionUtils.findField(sqlNode.getClass(), "defaultSqlNode");
                defaultSqlNodeField.setAccessible(true);
                cache.put("defaultSqlNode", defaultSqlNodeField);
            }

            ifSqlNodesField = (Field) cache.get("ifSqlNodes");
            if (null == ifSqlNodesField) {
                ifSqlNodesField = ReflectionUtils.findField(sqlNode.getClass(), "ifSqlNodes");
                ifSqlNodesField.setAccessible(true);
                cache.put("ifSqlNodes", ifSqlNodesField);
            }
        } else if (!(sqlNode instanceof StaticTextSqlNode)) {
            contentsField = ReflectionUtils.findField(sqlNode.getClass(), "contents");// 不能缓存，无法判断sqlNode类型
            if (null != contentsField) {
                contentsField.setAccessible(true);
            }
        }

        if (null != contentsField || null != defaultSqlNodeField || null != ifSqlNodesField) {
            if (null != defaultSqlNodeField) {
                Object contents = defaultSqlNodeField.get(sqlNode);
                if (null != contents) {
                    parameterKeys.addAll(parseSqlNode((SqlNode) contents));
                }
            }

            Object contents = null;
            if (null != contentsField) {
                contents = contentsField.get(sqlNode);
            } else if (null != ifSqlNodesField) {
                contents = ifSqlNodesField.get(sqlNode);
            }
            if (contents instanceof Collection) {
                List<SqlNode> mixedSqlNode = (List<SqlNode>) contents;
                for (SqlNode sql : mixedSqlNode) {
                    parameterKeys.addAll(parseSqlNode(sql));
                }
            } else if (null != contents) {
                parameterKeys.addAll(parseSqlNode((SqlNode) contents));
            }
        }
        return parameterKeys;
    }
}
