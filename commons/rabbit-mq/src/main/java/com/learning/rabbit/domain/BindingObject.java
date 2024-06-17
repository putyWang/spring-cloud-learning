package com.learning.rabbit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BindingObject
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BindingObject {
    public static final String DEFAULT_ROUTING_KEY = "#";
    private String defaultRoutingKey = "#";

    /**
     * 队列数组
     */
    private List<Queue> queues = new ArrayList<>();

    /**
     * 绑定数组
     */
    private List<Binding> bindings = new ArrayList<>();

    public void addBinding(String queueName, String exchange) {
        this.addBinding(queueName, exchange, null);
    }

    public void addBinding(String queueName, String exchange, String routingKey) {
        this.addBinding(queueName, exchange, routingKey, null);
    }

    public void addBinding(String queueName, String exchange, String routingKey, Map<String, Object> arguments) {
        if (StringUtils.isEmpty(queueName)) {
            throw new RuntimeException("invalid queueName:" + queueName);
        }

        if (StringUtils.isEmpty(exchange)) {
            throw new RuntimeException("invalid exchange:" + exchange);
        }

        String realRoutingKey = routingKey;

        if (StringUtils.isEmpty(routingKey)) {
            realRoutingKey = this.defaultRoutingKey;
        }

        this.addQueue(queueName);
        this.addBinding(new Binding(queueName, Binding.DestinationType.QUEUE, exchange, realRoutingKey, arguments));
    }

    private void addBinding(Binding binding) {
        bindings.add(binding);
    }

    /**
     * 新增队列
     * @param queueName
     */
    private void addQueue(String queueName) {
        queues.add(new Queue(queueName));
    }
}
