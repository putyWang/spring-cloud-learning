package com.learning.orm.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: UseStock
 * @Description:
 * @Author: WangWei
 * @Date: 2024-05-24
 * @Version V1.0
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UseStock {
}
