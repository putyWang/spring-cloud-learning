package com.learning.web.service;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.learning.core.domain.annotation.Query;
import com.learning.core.domain.annotation.UnionUnique;
import com.learning.core.domain.annotation.UnionUniqueCode;
import com.learning.core.domain.annotation.Unique;
import com.learning.core.domain.enums.ApiCode;
import com.learning.core.utils.CollectionUtils;
import com.learning.core.utils.ReflectionUtils;
import com.learning.core.utils.StringUtil;
import com.learning.web.eums.BaseOperationEnum;
import com.learning.web.except.ExceptionBuilder;
import com.learning.web.model.dto.BaseDto;
import com.learning.web.model.entity.BaseEntity;
import com.learning.web.model.param.PageParam;
import com.learning.web.model.param.SortPageParam;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public interface BaseService<D extends BaseDto, T extends BaseEntity>
        extends IService<T> {

    /**
     * 分页函数
     *
     * @param page
     * @return
     */
    default IPage<T> page(PageParam page, Wrapper<T> queryWrapper) {

        //将PageParam转化为IPage
        Page<T> iPage = getPage(page);

        //当设置了查询条件时直接查询
        if (queryWrapper != null) {

            return this.getBaseMapper().selectPage(iPage, queryWrapper);
        }

        //未设置查询条件时，根据page提供策略对wrapper进行重构
        Wrapper<T> wrapper = getWrapper(page);

        return this.getBaseMapper().selectPage(iPage, extensionWrapper(page, wrapper));
    }

    default Wrapper<T> extensionWrapper(PageParam page, Wrapper<T> wrapper) {

        return wrapper;
    }

    /**
     * 单条插入
     *
     * @param t
     * @return
     */
    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean insert(T t) {
        if (t == null) {

            return true;
        }

        processBeforeOperation(t, BaseOperationEnum.INSERT);
        checkUniqueField(t, false);
        boolean save = save(t);

        if (!save) {
            throw ExceptionBuilder.build("插入失败");
        } else {
            this.processAfterOperation(t, BaseOperationEnum.INSERT);
            this.clearBusinessCache(t, BaseOperationEnum.INSERT);
            return true;
        }
    }

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    boolean insertBatch(List<T> list);

    /**
     * 单条更新
     *
     * @param t
     * @return
     */
    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean update(T t) {
        if (t == null) {

            return true;
        }

        processBeforeOperation(t, BaseOperationEnum.UPDATE);
        checkUniqueField(t, true);

        if (t instanceof BaseEntity) {
            this.clearBusinessCache(t, BaseOperationEnum.DELETE);
        }

        boolean update = updateById(t);

        if (!update) {
            throw ExceptionBuilder.build("更新失败");
        } else {
            this.processAfterOperation(t, BaseOperationEnum.UPDATE);
            this.clearBusinessCache(t, BaseOperationEnum.UPDATE);
            return true;
        }
    }

    /**
     * 批量删除数据
     *
     * @param ids
     * @return
     */
    default boolean deleteBatch(List<Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return true;
        }

        List<T> ts = this.getBaseMapper().selectBatchIds(ids);

        if (CollectionUtils.isEmpty(ts)) {
            throw ExceptionBuilder.build("该对象不存在");
        }

        processBeforeBatchOperation(ts, BaseOperationEnum.BATCH_DELETE);
        boolean result = SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(ids));

        if (result) {
            ts.stream().forEach((t) -> {
                clearBusinessCache(t, BaseOperationEnum.BATCH_DELETE);
            });
            processAfterBatchOperation(ts, BaseOperationEnum.BATCH_DELETE);

            return true;
        } else
            throw ExceptionBuilder.build("删除失败");

    }

    /**
     * 前部钩子函数
     *
     * @param t
     * @param baseOperationEnum
     */
    default void processBeforeOperation(T t, BaseOperationEnum baseOperationEnum) {
    }

    /**
     * 处理后钩子函数
     *
     * @param t
     * @param baseOperationEnum
     */
    default void processAfterOperation(T t, BaseOperationEnum baseOperationEnum) {
    }

    /**
     * 批量前部钩子函数
     *
     * @param list
     * @param baseOperationEnum
     */
    default void processBeforeBatchOperation(List<T> list, BaseOperationEnum baseOperationEnum) {
    }

    /**
     * 处理后钩子函数
     *
     * @param list
     * @param baseOperationEnum
     */
    default void processAfterBatchOperation(List<T> list, BaseOperationEnum baseOperationEnum) {
    }

    /**
     * 进行非重验证
     *
     * @param entity
     * @param isUpdate
     */
    default void checkUniqueField(T entity, boolean isUpdate) {
        Field[] allFields = ReflectionUtils.getAllFieldsArr(entity);

        //获取相应的表id字段
        Optional<Field> idFiledOptional = Arrays.stream(allFields)
                .filter((field) -> {
                    return field.isAnnotationPresent(TableId.class);
                }).findFirst();

        //验证id是否不重复
        if (idFiledOptional.isPresent()) {
            Field idField = idFiledOptional.get();
            idField.setAccessible(true);

            for (int i = 0; i < allFields.length; ++i) {
                Field field = allFields[i];

                if (field.isAnnotationPresent(Unique.class)) {
                    Unique unique = field.getDeclaredAnnotation(Unique.class);
                    QueryWrapper wrapper = Wrappers.query();

                    try {
                        Object value = this.getFieldValue(entity, field);
                        String column;
                        if (StringUtil.isBlank(unique.column())) {
                            column = com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(field.getName());
                        } else {
                            column = unique.column();
                        }

                        wrapper.eq(column, value);
                        if (isUpdate) {
                            wrapper.ne((idField.getAnnotation(TableId.class)).value(), idField.get(entity));
                        }
                    } catch (Exception E) {
                        continue;
                    }

                    if (this.getBaseMapper().selectCount(wrapper) > 0) {
                        String errorMeg = unique.code();
                        if (StringUtil.isBlank(errorMeg)) {
                            errorMeg = unique.apiCode().getMessage();
                        }

                        throw ExceptionBuilder.build(ApiCode.DAO_EXCEPTION.getCode(), errorMeg, new Object[]{field.getName()});
                    }
                }
            }

            // 存储组名称
            Map<String, QueryWrapper<T>> unionUniqueMap = new HashMap();

            //为相应组设置相应查询语句
            for (int i = 0; i < allFields.length; ++i) {
                Field field = allFields[i];
                if (field.isAnnotationPresent(UnionUnique.class)) {
                    try {
                        UnionUnique[] unionUniques = field.getDeclaredAnnotationsByType(UnionUnique.class);

                        for (int j = 0; j < unionUniques.length; ++j) {
                            UnionUnique unionUnique = unionUniques[j];
                            String group = unionUnique.group();
                            Object value = this.getFieldValue(entity, field);
                            String column;

                            if (StringUtil.isBlank(unionUnique.column())) {
                                column = com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(field.getName());
                            } else {
                                column = unionUnique.column();
                            }

                            QueryWrapper unionWrapper;
                            if (unionUniqueMap.containsKey(group)) {
                                unionWrapper = unionUniqueMap.get(group);
                                unionWrapper.eq(column, value);
                            } else {
                                unionWrapper = Wrappers.query();
                                unionWrapper.eq(column, value);
                                unionUniqueMap.put(group, unionWrapper);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }


            Set<Map.Entry<String, QueryWrapper<T>>> entries = unionUniqueMap.entrySet();
            Iterator iterator = entries.iterator();

            while (true) {
                Map.Entry entry;
                Long result;
                do {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    entry = (Map.Entry) iterator.next();
                    QueryWrapper<T> queryWrapper = (QueryWrapper) entry.getValue();
                    if (isUpdate) {
                        try {
                            queryWrapper.ne((idField.getAnnotation(TableId.class)).value(), idField.get(entity));
                        } catch (Exception e) {
                            return;
                        }
                    }

                    result = this.getBaseMapper().selectCount(queryWrapper);
                } while (result <= 0);

                String group = (String) entry.getKey();
                Class<? extends BaseEntity> aClass = entity.getClass();
                UnionUniqueCode[] unionUniqueCodes = aClass.getAnnotationsByType(UnionUniqueCode.class);

                for (int i = 0; i < unionUniqueCodes.length; ++i) {
                    UnionUniqueCode unionUniqueCode = unionUniqueCodes[i];
                    if (StringUtil.equals(unionUniqueCode.group(), group)) {
                        throw ExceptionBuilder.build(unionUniqueCode.code());
                    }
                }
            }
        }
    }

    /**
     * 清除缓存
     *
     * @param entity
     * @param baseOperationEnum
     */
    default void clearBusinessCache(T entity, BaseOperationEnum baseOperationEnum) {
    }

    /**
     * 将page中的搜索策略加入到warpper中
     *
     * @param page
     * @return
     */
    default Wrapper<T> getWrapper(PageParam page) {

        QueryWrapper<T> queryWrapper = new QueryWrapper();
        //获取所有字段
        Field[] declaredFields = page.getClass().getDeclaredFields();

        Arrays.stream(declaredFields).filter((field) -> {
            if (field.isAnnotationPresent(Query.class)) {
                Query query = field.getAnnotation(Query.class);
                return query.where();
            } else
                return false;
        }).forEach((field) -> {
            try {
                field.setAccessible(true);
                String column;

                //设置字段名
                if (field.isAnnotationPresent(Query.class)
                        && !StringUtil.isBlank(field.getAnnotation(Query.class).column())) {
                    column = field.getAnnotation(Query.class).column();
                } else {
                    column = com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(field.getName());
                }

                boolean flag;
                Object o = field.get(page);
                //判断字段值是否为字符串，或者是否为list
                if (o instanceof String) {

                    flag = !StringUtil.isNoneBlank((String) o);
                } else
                    flag = o == null;

                if (flag)
                    return;

                //设置查询条件
                if (field.isAnnotationPresent(Query.class)) {
                    switch (field.getAnnotation(Query.class).value()) {
                        case LIKE:
                            String valueLike = String.valueOf(o);
                            if (valueLike.contains("%")) {
                                valueLike = valueLike.replace("%", "\\%");
                            } else if (valueLike.contains("_")) {
                                valueLike = valueLike.replace("_", "\\_");
                            }

                            queryWrapper.like(column, valueLike);
                            break;
                        case IN:
                            Object value = o;

                            if (value instanceof List) {
                                queryWrapper.in(column, value);
                            } else if (value instanceof String) {
                                String[] split = ((String) value).split(",");
                                List<String> list = Arrays.asList(split);
                                queryWrapper.in(column, list);
                            }

                            break;
                        case GT:
                            queryWrapper.gt(column, o);
                            break;
                        case GE:
                            queryWrapper.ge(column, o);
                            break;
                        case LT:
                            queryWrapper.lt(column, o);
                            break;
                        case LE:
                            queryWrapper.le(column, o);
                            break;
                        case BWT:
                            String[] split = o.toString().split(",");
                            if (split.length == 2) {
                                queryWrapper.between(column, split[0], split[1]);
                            } else if (split.length == 1) {
                                queryWrapper.ge(column, split[0]);
                            }
                            break;
                        default:
                            queryWrapper.eq(column, o);
                    }
                } else {
                    queryWrapper.eq(column, o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return queryWrapper;
    }

    /**
     * 根据PageParam获取page
     *
     * @param page
     * @return
     */
    default Page<T> getPage(PageParam page) {

        Page<T> iPage = new Page<>();
        iPage.setCurrent(page.getPageNumber());
        iPage.setSize(page.getPageSize());

        //当分页中需要排序时
        if (page instanceof SortPageParam) {
            SortPageParam sortPage = (SortPageParam) page;
            List<String> sorts = sortPage.getSorts();
            List<String> acSs = sortPage.getAcsList();

            if (CollectionUtils.isNotEmpty(sorts)) {
                OrderItem[] orderItems = new OrderItem[sorts.size()];
                //设置排序规则
                if (CollectionUtils.isEmpty(acSs)) {
                    for (int i = 0; i < sorts.size(); i++) {
                        orderItems[i] = build(sorts.get(i), "ASC");
                    }
                } else if (acSs.size() < sorts.size()) {
                    for (int i = 0; i < sorts.size(); i++) {
                        orderItems[i] = build(sorts.get(i), acSs.get(0));
                    }
                } else {
                    for (int i = 0; i < sorts.size(); i++) {
                        orderItems[i] = build(sorts.get(i), acSs.get(i));
                    }
                }

                iPage.addOrder(orderItems);
            }
        }

        return iPage;
    }

    /**
     * 建立
     *
     * @param sort
     * @param order
     * @return
     */
    default OrderItem build(String sort, String order) {

        //将sort转化为表字段的形式
        String column = com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(sort);

        if ("ASC" == order) {

            return OrderItem.asc(column);
        } else

            return OrderItem.desc(column);
    }

    /**
     * 获取相应字段值
     *
     * @param entity
     * @param field
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    default Object getFieldValue(T entity, Field field)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor propertyDescriptor =
                new PropertyDescriptor(field.getName(), entity.getClass());

        Method readMethod = propertyDescriptor.getReadMethod();

        return readMethod.invoke(entity);
    }

}
