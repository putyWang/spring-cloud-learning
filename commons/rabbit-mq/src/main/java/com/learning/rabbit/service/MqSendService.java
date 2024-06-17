package com.learning.rabbit.service;

import com.learning.rabbit.domain.BaseMqMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: MqSendService
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@RequiredArgsConstructor
public class MqSendService {

    private final RabbitMqService rabbitMqService;

    public void sendMessage(BaseMqMessage mqMessage) {
        this.rabbitMqService.sendMessage(mqMessage);
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        this.rabbitMqService.sendMessage(exchange, routingKey, message);
    }
}
