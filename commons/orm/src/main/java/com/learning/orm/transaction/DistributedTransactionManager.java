package com.learning.orm.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * @author WangWei
 * @version v 1.0
 * @description 分布式事物控制器
 * @date 2024-07-30
 **/
@RequiredArgsConstructor
public class DistributedTransactionManager extends DataSourceTransactionManager {

    private final DataSourceTransactionManager dataSourceTransactionManager;

    @Override
    protected Object doGetTransaction() throws TransactionException {
        return super.doGetTransaction();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {

    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {

    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {

    }
}
