//package com.learning.service.impl;
//
//import com.learning.conditions.Wrapper;
//import com.learning.service.IService;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.GenericTypeResolver;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.Collection;
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.Function;
//
///**
// * @author wangwei
// * @version 1.0
// * @date 2025/6/1 下午5:40
// */
//public class ServiceImpl<R extends ReactiveCrudRepository<T, ID>, T> implements IService<T> {
//
//    protected final Log log = LogFactory.getLog(this.getClass());
//
//    @Autowired
//    protected R baseMapper;
//
//    /**
//     * 通过泛型解析得到当前状态
//     */
//    protected final Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(this.getClass(), ServiceImpl.class);
//
//    protected final Class<T> entityClass = this.currentEntityClass();
//
//    protected final Class<R> repositoryClass = this.currentRepositoryClass();
//
//    protected Class<R> currentRepositoryClass() {
//        return (Class<R>) this.typeArguments[0];
//    }
//
//    protected Class<T> currentEntityClass() {
//        return (Class<T>) this.typeArguments[1];
//    }
//
//    @Override
//    public Mono<Boolean> save(T entity) {
//        return baseMapper.save(entity).map(T -> true).onErrorComplete(
//                e -> {
//                    log.error("保存对象失败", e);
//                    return false;
//                }
//        );
//    }
//
//    @Override
//    public Mono<Boolean> saveBatch(Collection<T> entityList, int batchSize) {
//        return Flux.fromIterable(entityList).buffer(batchSize).flatMap(
//                list -> baseMapper.saveAll(list)
//        ).then(Mono.just(true))
//                .onErrorComplete(
//                        e -> {
//                            log.error("批量保存数据失败", e);
//                            return false;
//                        }
//                );
//    }
//
//    @Override
//    public Mono<Boolean> saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
//        return saveBatch(entityList, batchSize);
//    }
//
//    @Override
//    public Mono<Boolean> updateBatchById(Collection<T> entityList, int batchSize) {
//        return saveBatch(entityList, batchSize);
//    }
//
//    @Override
//    public Mono<Boolean> saveOrUpdate(T entity) {
//        return save(entity);
//    }
//
//    @Override
//    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
//        return null;
//    }
//
//    @Override
//    public Optional<T> getOneOpt(Wrapper<T> queryWrapper, boolean throwEx) {
//        return Optional.empty();
//    }
//
//    @Override
//    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
//        return Map.of();
//    }
//
//    @Override
//    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
//        return null;
//    }
//
//    @Override
//    public BaseMapper<T> getBaseMapper() {
//        return null;
//    }
//
//    @Override
//    public Class<T> getEntityClass() {
//        return null;
//    }
//}
