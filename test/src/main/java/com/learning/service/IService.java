package com.learning.service;

import cn.hutool.core.lang.Assert;
import com.learning.conditions.Wrapper;
import com.learning.conditions.Wrappers;
import com.learning.repository.IRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/6/1 上午11:07
 */
public interface IService<T, ID extends Serializable> extends IRepository<T, ID> {

        /**
         * 插入（批量）
         *
         * @param entityList 实体对象集合
         */
        default Mono<Boolean> saveBatch(Collection<T> entityList) {
            return saveBatch(entityList, DEFAULT_BATCH_SIZE);
        }

        /**
         * 批量修改插入
         *
         * @param entityList 实体对象集合
         */
        default Mono<Boolean> saveOrUpdateBatch(Collection<T> entityList) {
            return saveOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
        }

        /**
         * 批量删除(jdbc批量提交)
         *
         * @param list 主键ID或实体列表(主键ID类型必须与实体类型字段保持一致)
         * @return 删除结果
         * @since 3.5.0
         */
        default Mono<Void> removeBatchByIds(Collection<ID> list) {
            return removeByIds(list);
        }

        /**
         * 根据ID 批量更新
         *
         * @param entityList 实体对象集合
         */
        default Mono<Boolean> updateBatchById(Collection<T> entityList) {
            return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
        }
    }