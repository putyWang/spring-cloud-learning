package com.learning.web.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.core.utils.CollectionUtils;
import com.learning.web.eums.BaseOperationEnum;
import com.learning.web.except.ExceptionBuilder;
import com.learning.web.mapper.RootMapper;
import com.learning.web.model.dto.BaseDto;
import com.learning.web.model.entity.BaseEntity;
import com.learning.web.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wangwei
 * @Description:
 */
public abstract class BaseServiceImpl<M extends RootMapper<T>, T extends BaseEntity, D extends BaseDto>
        extends ServiceImpl<M, T>
        implements BaseService<D, T> {

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public boolean insertBatch(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {

            return true;
        } else {
            this.processBeforeBatchOperation(list, BaseOperationEnum.BATCH_INSERT);
            boolean result = list.size() == this.baseMapper.insertBatch(list);
            list.forEach((entity) -> {
                this.clearBusinessCache(entity, BaseOperationEnum.INSERT);
            });
            if (!result) {
                throw ExceptionBuilder.build("插入失败");
            } else {
                this.processAfterBatchOperation(list, BaseOperationEnum.BATCH_INSERT);
                return true;
            }
        }
    }
}
