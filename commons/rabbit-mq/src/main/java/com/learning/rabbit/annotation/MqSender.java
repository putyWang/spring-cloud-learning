package com.learning.rabbit.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: MqSender
 * @Description: 发送者
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MqSender {
    String exchange();

    String routingKey() default "#";

    boolean isAsync() default false;
}
