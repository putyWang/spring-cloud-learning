package com.learning.rabbitmq.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName: MqMessageConverter
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Log4j2
public class MqMessageConverter extends SimpleMessageConverter {
    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();

    @Override
    public @NotNull Object fromMessage(Message message) throws MessageConversionException {
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    @Override
    protected @NotNull Message createMessage(@NotNull Object object, @NotNull MessageProperties messageProperties) {
        try {
            return super.createMessage(OBJECTMAPPER.writeValueAsString(object), messageProperties);
        } catch (JsonProcessingException e) {
            log.error("Could not write JSON");
            throw new RuntimeException(e);
        }
    }
}

