package com.learning.orm.annotation;

import com.learning.orm.enums.TableTypeEnum;

import java.lang.annotation.*;

/**
 * @ClassName: Independent
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Independent {
    TableTypeEnum type() default TableTypeEnum.SINGLE_TABLE;

    String dateKey() default "";

    String partitionKey() default "";

    String primaryKey() default "";

    int partitionNum() default -1;

    String databseName();

    String tableName();

    String wardKey() default "";
}

