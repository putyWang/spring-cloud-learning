package com.learning.config.ws.handler;

import com.learning.config.ws.model.WsSessionHolder;
import com.learning.service.WsMsgHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/25 下午6:07
 */
@Component
@Order(Integer.MIN_VALUE)
public class respHandler implements WsMsgHandler {
    @Override
    public boolean process(String message, WebSocketSession session) {
        WsSessionHolder.sendMsg("test" + message);
        return true;
    }
}
