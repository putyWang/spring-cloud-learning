package com.learning.rabbitmq.domain;

/**
 * @author WangWei
 * @version v 1.0
 * @description 交换机主题类型
 * @date 2024-07-29
 **/
public enum ExchangeTypeEnum {
    /**
     * 三个主题 默认使用 TOPIC
     */
    TOPIC,FANOUT,DIRECT
}
