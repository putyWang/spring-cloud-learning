package com.learning.repository;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/2 上午12:14
 */
import cn.hutool.core.lang.Assert;
import com.learning.orm.core.constant.Constants;
import com.learning.orm.repository.AbstractRepository;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;

/**
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ）
 *
 * @author hubin
 * @since 2018-06-23
 */
public abstract class CrudRepository<M extends ReactiveCrudRepository<T, ID>, T, ID extends Serializable>
        extends AbstractRepository<T, ID> {

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Override
    public Mono<Boolean> saveBatch(Collection<T> entityList, int batchSize) {
        return Flux.fromIterable(entityList)
                .buffer(batchSize)
                .flatMap(list -> getBaseMapper().saveAll(list).then(Mono.just(true)))
                .then(Mono.just(true));
    }

    @Override
    public Mono<Boolean> saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        return saveBatch(entityList, batchSize);
    }

    @Override
    public Mono<Boolean> updateBatchById(Collection<T> entityList, int batchSize) {
        return saveBatch(entityList, batchSize);
    }
}

