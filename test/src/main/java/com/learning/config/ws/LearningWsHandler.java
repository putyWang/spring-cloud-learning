package com.learning.config.ws;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.learning.config.ws.model.WsSessionHolder;
import com.learning.service.WsMsgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.socket.*;

import java.util.List;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/24 下午9:11
 */
@Log4j2
@RequiredArgsConstructor
public class LearningWsHandler implements WebSocketHandler {

    private final List<WsMsgHandler> handlerChain;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("正在创建{}的链接,远端地址为{},链接建立uri:{}", session.getId(), session.getRemoteAddress(), session.getUri());
        WsSessionHolder.putSession(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        // 收到消息时首先更新下当前活跃时间
        WsSessionHolder.updateActiveTime(session.getId());

        if (ObjectUtil.isNull(message) || message instanceof PongMessage) {
            return;
        }

        String msg;
        try {
            msg = JSON.toJSONString(message.getPayload());
        } catch (Exception e) {
            msg = String.valueOf(message);
        }
        log.info("{}链接接收到{}消息", session.getId(), msg);
        if (CollUtil.isNotEmpty(handlerChain)) {
            WsSessionHolder.setQueryModel(session);
            for (WsMsgHandler handler : handlerChain) {
                if (handler.process(msg, session)) {
                    break;
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("{}链接正在关闭,类型为{}", session.getId(), closeStatus.getCode());
        WsSessionHolder.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
