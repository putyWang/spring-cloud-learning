package com.learning.web.config.mybatis.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.learning.web.config.mybatis.mapper.InsertBatchMethod;
import com.learning.web.config.mybatis.mapper.UpdateBatchMethod;

import java.util.List;

public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatchMethod());
        methodList.add(new UpdateBatchMethod());
        return methodList;
    }
}
