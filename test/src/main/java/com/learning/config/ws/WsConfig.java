package com.learning.config.ws;

import com.learning.config.ws.thread.ActiveCheckThread;
import com.learning.service.WsMsgHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/24 下午8:53
 */
@Configuration
@ConditionalOnBean(WsProperties.class)
//@EnableWebSocketMessageBroker
// 启用原生 WebSocket
@EnableWebSocket
@RequiredArgsConstructor
public class WsConfig implements WebSocketConfigurer {

    private final WsProperties wsProperties;

    private final List<WsMsgHandler> handlerList;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LearningWsHandler(handlerList), wsProperties.getEndpoint())
                .setAllowedOriginPatterns(wsProperties.getAllowedOriginPatterns());
//                .withSockJS();  // 支持HTTP轮询降级[6,8](@ref);
    }

    @Bean
    public ActiveCheckThread activeCheckThread() {
        return new ActiveCheckThread(wsProperties.getTimeOut(), wsProperties.getHeartBeatTime());
    }
}
