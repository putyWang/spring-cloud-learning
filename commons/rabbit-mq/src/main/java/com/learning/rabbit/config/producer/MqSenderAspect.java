package com.learning.rabbit.config.producer;

import com.learning.rabbit.annotation.MqSender;
import com.learning.rabbit.service.AsyncMqSendService;
import com.learning.rabbit.service.MqSendService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MqSenderAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqSenderAspect.class);
    @Autowired
    private AsyncMqSendService asyncMqSendService;
    @Autowired
    private MqSendService mqSendService;

    public MqSenderAspect() {
    }

    @Pointcut("@annotation(com.yanhua.cloud.rabbitmq.annotation.MqSender)")
    public void mqSenderCut() {
        System.out.println("aaa");
    }

    @AfterReturning(
            pointcut = "mqSenderCut()",
            returning = "object"
    )
    public void afterReturning(JoinPoint point, Object object) throws Throwable {
        if (null == object) {
            LOGGER.debug(String.format("error, return message is null"));
        } else {
            Annotation[] annotations = ((MethodSignature)point.getStaticPart().getSignature()).getMethod().getAnnotations();
            Annotation[] var4 = annotations;
            int var5 = annotations.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Annotation annotation = var4[var6];
                if (annotation.annotationType().equals(MqSender.class)) {
                    this.sendMessage((MqSender)annotation, object);
                }
            }

        }
    }

    @AfterThrowing(
            pointcut = "mqSenderCut()",
            throwing = "exception"
    )
    public void afterThrowing(Exception exception) throws Throwable {
        LOGGER.error(String.format("mqSenderCut() is throw a exception:%s", exception.toString()));
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
