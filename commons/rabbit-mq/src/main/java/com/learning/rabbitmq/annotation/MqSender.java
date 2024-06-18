package com.learning.rabbitmq.annotation;

import java.lang.annotation.*;

/**
 * @description 发送者
 * @author WangWei
 * @date 2024-06-17
 * @version V1.0
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
