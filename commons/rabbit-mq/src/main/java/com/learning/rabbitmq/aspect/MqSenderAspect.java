package com.learning.rabbitmq.aspect;

import com.learning.core.utils.ObjectUtils;
import com.learning.rabbitmq.annotation.MqSender;
import com.learning.rabbitmq.service.MqSendService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @ClassName: MqSenderAspect
 * @Description:
 * @Author: WangWei
 * @Date: 2024-06-17
 * @Version V1.0
 **/
@Aspect
@Log4j2
@AllArgsConstructor
public class MqSenderAspect {

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

        sendMessage(((MethodSignature)point.getStaticPart().getSignature()).getMethod().getAnnotation(MqSender.class), object);
    }

    @AfterThrowing(
            pointcut = "mqSenderCut()",
            throwing = "exception"
    )
    public void afterThrowing(Exception exception) {
        log.error("mqSenderCut() is throw a exception", exception);
    }

    private void sendMessage(MqSender mqSender, Object object) {
        if(ObjectUtils.isNull(mqSender)){
            log.error("MqSender 为空");
        } else if (mqSender.isAsync()) {
            this.mqSendService.sendMessageAsync(mqSender.exchange(), mqSender.routingKey(), object);
        } else {
            this.mqSendService.sendMessage(mqSender.exchange(), mqSender.routingKey(), object);
        }

    }
}
