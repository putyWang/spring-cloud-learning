package com.learning.orm.repository;


import com.learning.orm.core.conditions.Wrapper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractRepository<T, ID extends Serializable> implements IRepository<T, ID> {

    /**
     * @see #getEntityClass()
     */
    private Class<T> entityClass;

    @Override
    public Class<T> getEntityClass() {
        if(this.entityClass == null) {
            this.entityClass = (Class<T>) GenericTypeResolver.resolveTypeArguments(this.getMapperClass(), ReactiveCrudRepository.class)[0];
        }
        return this.entityClass;
    }

    /**
     *  @see #getMapperClass()
     */
    private Class<ReactiveCrudRepository<T, ID>> mapperClass;

    /**
     * @return baseMapper 真实类型
     * @since 3.5.7
     */
    public Class<ReactiveCrudRepository<T, ID>> getMapperClass() {
        if (this.mapperClass == null) {
//            this.mapperClass = this.getBaseMapper().getClass().get;
        }
        return this.mapperClass;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public Mono<Boolean> saveOrUpdate(T entity) {
        return save(entity);
    }
}
