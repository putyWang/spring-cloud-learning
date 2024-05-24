package com.learning.orm.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: TableCode
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableCode {
    String value();
}
