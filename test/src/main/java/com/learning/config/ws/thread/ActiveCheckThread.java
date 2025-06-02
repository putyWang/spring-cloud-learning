package com.learning.config.ws.thread;

import com.learning.config.ws.model.WsSessionHolder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/25 下午10:13
 */
public class ActiveCheckThread implements DisposableBean, InitializingBean {

    /**
     * 链接超时时间
     * 默认 45 秒
     */
    private final Long timeOut;

    /**
     * 心跳检测时间
     * 默认 15 秒
     */
    private final Long heartBeatTime;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ActiveCheckThread(Long timeOut, Long heartBeatTime) {
        this.timeOut = timeOut;
        this.heartBeatTime = heartBeatTime;
    }

    @Override
    public void afterPropertiesSet() {
        scheduler.scheduleAtFixedRate(
                () -> WsSessionHolder.idleCheck(timeOut, heartBeatTime),
                0, 10, TimeUnit.SECONDS
        );
    }

    @Override
    public void destroy() {
        scheduler.shutdown();
    }
}
