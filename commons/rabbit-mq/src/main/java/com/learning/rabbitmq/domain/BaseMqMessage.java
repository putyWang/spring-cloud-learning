package com.learning.rabbitmq.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName: BaseMqMessage
 * @Description: 消息封装对象
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Getter
@Setter
public class BaseMqMessage implements Serializable {
    /**
     * 交换名
     */
    private String exchange;

    /**
     * 路由键值
     */
    private String routingKey;

    /**
     * 动作类型
     */
    private String eventType;
}
