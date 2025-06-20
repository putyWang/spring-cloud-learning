package com.learning.orm.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.learning.orm.core.conditions.Wrapper;
import com.learning.orm.core.constant.enums.SQLType;
import com.learning.orm.core.metadata.table.TableInfoHolder;
import com.learning.orm.core.metadata.table.TableInfoMetadata;
import com.learning.orm.service.IService;
import com.learning.orm.util.sql.SqlUtils;
import com.learning.repository.CrudRepository;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 下午5:40
 */
public class ServiceImpl<M extends ReactiveCrudRepository<T, ID>, T, ID extends Serializable>
        extends CrudRepository<M, T, ID>
        implements IService<T, ID> {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    protected M baseMapper;

    @Autowired
    protected ConnectionFactory connectionFactory;

    private TableInfoMetadata tableInfoMetadata;

    @Override
    public M getBaseMapper() {
        Assert.notNull(this.baseMapper, "baseMapper can not be null");
        return this.baseMapper;
    }

    public TableInfoMetadata getTableInfoMetadata() {
        if (ObjectUtil.isNull(tableInfoMetadata)) {
            tableInfoMetadata = TableInfoHolder.obtainTableInfo(getEntityClass());
        }
        return tableInfoMetadata;
    }

    /**
     * 通过泛型解析得到当前状态
     */
    protected final Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(this.getClass(), ServiceImpl.class);

    protected final Class<T> entityClass = this.currentEntityClass();

    protected final Class<M> repositoryClass = this.currentRepositoryClass();

    protected Class<M> currentRepositoryClass() {
        return (Class<M>) this.typeArguments[0];
    }

    protected Class<T> currentEntityClass() {
        return (Class<T>) this.typeArguments[1];
    }

    @Override
    public Mono<Boolean> removeByMap(Map<String, Object> columnMap) {
        return Mono.from(connectionFactory.create())
                .flatMap(connection -> {
                    String sql = SQLType.DELETE.buildBaseSql(getTableInfoMetadata())
                            + SqlUtils.buildWhere(columnMap);

                })
    }

    @Override
    public Mono<Boolean> remove(Wrapper<T> queryWrapper) {
        return false;
    }

    @Override
    public Mono<Boolean> update(T entity, Wrapper<T> updateWrapper) {
        return null;
    }

    @Override
    public Flux<T> listByMap(Map<String, Object> columnMap) {
        return null;
    }

    @Override
    public Mono<T> getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public <V> Mono<V> getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public Mono<Boolean> exists(Wrapper<T> queryWrapper) {
        return null;
    }

    @Override
    public Mono<Long> count(Wrapper<T> queryWrapper) {
        return null;
    }

    @Override
    public Flux<T> list(Wrapper<T> queryWrapper) {
        return null;
    }

    @Override
    public <E> Flux<E> listObjs() {
        return null;
    }

    @Override
    public <V> Flux<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    private void execute(String prefix, Map<String, Object> columnMap) {
        Mono<Connection> connectionMono = (Mono<Connection>) connectionFactory.create();
        connectionMono.flatMap(
                conn ->
        )
    }

    /**
     * sql 语句执行
     * @param sql 需要执行的 sql
     * @param columnList 字段对应值列表
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private Mono<Result> execute(String sql, List<Object> columnList) {
        Mono<Connection> connectionMono = (Mono<Connection>) connectionFactory.create();
        return connectionMono.flatMap(
                conn -> Mono.usingWhen(
                        Mono.just(conn),
                        c -> {
                            Statement statement = c.createStatement(sql);

                            for (int i = 0; i < columnList.size(); i++) {
                                statement.bind(i, columnList.get(i));
                            }
                            return (Mono<Result>) statement.execute();
                        },
                        Connection::close
                )
        ).onErrorResume(e -> {
            log.error("SQL执行失败", e);
            return Mono.error(e);
        });
    }
}
