package com.learning.orm.repository;

import cn.hutool.core.collection.CollUtil;
import com.learning.orm.core.conditions.Wrapper;
import com.learning.orm.core.conditions.Wrappers;
import com.learning.orm.core.conditions.query.QueryWrapper;
import com.learning.orm.core.constant.Constants;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface IRepository<T, ID extends Serializable> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = Constants.DEFAULT_BATCH_SIZE;

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     */
    default Mono<Boolean> save(T entity) {
        return getBaseMapper().save(entity)
                .map(v -> true)
                .onErrorComplete(e -> false);
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    Mono<Boolean> saveBatch(Collection<T> entityList, int batchSize);

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    Mono<Boolean> saveOrUpdateBatch(Collection<T> entityList, int batchSize);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    default Mono<Void> removeById(ID id) {
        return getBaseMapper().deleteById(id);
    }

    /**
     * 根据 ID 删除
     *
     * @param id      主键(类型必须与实体类型字段保持一致)
     * @param useFill 是否启用填充(为true的情况,会将入参转换实体进行delete删除)
     * @return 删除结果
     * @since 3.5.0
     */
    default Mono<Boolean> removeById(Serializable id, boolean useFill) {
        throw new UnsupportedOperationException("不支持的方法!");
    }

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体
     * @since 3.4.4
     */
    default Mono<Void> removeById(T entity) {
        return getBaseMapper().delete(entity);
    }

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
    Mono<Boolean> removeByMap(Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param queryWrapper 实体包装类
     */
    Mono<Boolean> remove(Wrapper<T> queryWrapper);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param list 主键ID或实体列表
     */
    default Mono<Void> removeByIds(Collection<ID> list) {
        if (CollUtil.isEmpty(list)) {
            return Mono.empty();
        }
        return getBaseMapper().deleteAllById(list);
    }

    /**
     * 根据 ID 选择修改
     *
     * @param entity 实体对象
     */
    default Mono<Boolean> updateById(T entity) {
        return save(entity);
    }

    /**
     * 根据 UpdateWrapper 条件，更新记录 需要设置sqlset
     * <p>此方法无法进行自动填充,如需自动填充请使用{@link #update(Object, Wrapper)}</p>
     *
     * @param updateWrapper 实体对象封装操作类
     */
    default Mono<Boolean> update(Wrapper<T> updateWrapper) {
        return update(null, updateWrapper);
    }

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象(当entity为空时无法进行自动填充)
     * @param updateWrapper 实体对象封装操作类
     */
    Mono<Boolean> update(T entity, Wrapper<T> updateWrapper);

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    Mono<Boolean> updateBatchById(Collection<T> entityList, int batchSize);

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    Mono<Boolean> saveOrUpdate(T entity);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    default Mono<T> getById(ID id) {
        return getBaseMapper().findById(id);
    }

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表
     */
    default Flux<T> listByIds(Collection<ID> idList) {
        return getBaseMapper().findAllById(idList);
    }

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     */
    Flux<T> listByMap(Map<String, Object> columnMap);

    /**
     * 根据 Wrapper，查询一条记录 <br/>
     * <p>结果集，如果是多个会抛出异常，随机取一条加上限制条件 wrapper.last("LIMIT 1")</p>
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    default Mono<T> getOne(Wrapper<T> queryWrapper) {
        return getOne(queryWrapper, true);
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @param throwEx      有多个 result 是否抛出异常
     */
    Mono<T> getOne(Wrapper<T> queryWrapper, boolean throwEx);

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @param mapper       转换函数
     */
    <V> Mono<V> getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

    /**
     * 查询指定条件是否存在数据
     *
     * @see Wrappers#emptyWrapper()
     */
    Mono<Boolean> exists(Wrapper<T> queryWrapper);

    /**
     * 查询总记录数
     *
     * @see Wrappers#emptyWrapper()
     */
    default Mono<Long> count() {
        return getBaseMapper().count();
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类
     */
    Mono<Long> count(Wrapper<T> queryWrapper);

    /**
     * 查询列表
     *
     * @param queryWrapper 实体对象封装操作类
     */
    Flux<T> list(Wrapper<T> queryWrapper);

    /**
     * 查询所有
     *
     * @see Wrappers#emptyWrapper()
     */
    default Flux<T> list() {
        return getBaseMapper().findAll();
    }

    /**
     * 查询全部记录
     */
    <E> Flux<E> listObjs();

    /**
     * 查询全部记录
     *
     * @param mapper 转换函数
     */
    default <V> Flux<V> listObjs(Function<? super Object, V> mapper) {
        return listObjs(Wrappers.emptyWrapper(), mapper);
    }

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @param mapper       转换函数
     */
    <V> Flux<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    ReactiveCrudRepository<T, ID> getBaseMapper();

    /**
     * 获取 entity 的 class
     *
     * @return {@link Class<T>}
     */
    Class<T> getEntityClass();
}
