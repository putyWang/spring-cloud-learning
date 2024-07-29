package com.learning.rabbitmq.config.producer;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @ClassName: RabbitMqReturnCallback
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Log4j2
public class RabbitMqReturnCallback implements RabbitTemplate.ReturnsCallback {
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("--------------mq message can't be delivered to any queue--------------");
        log.error(String.format(
                "message=[%s]\n exchange=[%s]\n routingKey=[%s]\n replyCode=[%s]\n replyText=[%s]",
                returned.getMessage(),
                returned.getExchange(),
                returned.getRoutingKey(),
                returned.getReplyCode(),
                returned.getReplyText()
        ));
    }
}
