package com.learning.rabbitmq.config.producer;

import com.learning.rabbitmq.annotation.MqSender;
import com.learning.rabbitmq.service.AsyncMqSendService;
import com.learning.rabbitmq.service.MqSendService;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;

/**
 * @ClassName: MqSenderAspect
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Aspect
@Log4j2
public class MqSenderAspect {
    @Autowired
    private AsyncMqSendService asyncMqSendService;
    @Autowired
    private MqSendService mqSendService;

    @Pointcut("@annotation(com.learning.rabbitmq.annotation.MqSender)")
    public void mqSenderCut() {}

    @AfterReturning(
            pointcut = "mqSenderCut()",
            returning = "object"
    )
    public void afterReturning(JoinPoint point, Object object) {
        if (null == object) {
            log.debug("error, return message is null");
        }

        for(Annotation annotation : ((MethodSignature)point.getStaticPart().getSignature()).getMethod().getAnnotations()) {
            if (annotation.annotationType().equals(MqSender.class)) {
                this.sendMessage((MqSender)annotation, object);
            }
        }
    }

    @AfterThrowing(
            pointcut = "mqSenderCut()",
            throwing = "exception"
    )
    public void afterThrowing(Exception exception) throws Throwable {
        log.error("mqSenderCut() is throw a exception");
        throw exception;
    }

    private void sendMessage(MqSender mqSender, Object object) {
        if (mqSender.isAsync()) {
            this.asyncMqSendService.sendMessage(mqSender.exchange(), mqSender.routingKey(), object);
        } else {
            this.mqSendService.sendMessage(mqSender.exchange(), mqSender.routingKey(), object);
        }

    }
}
