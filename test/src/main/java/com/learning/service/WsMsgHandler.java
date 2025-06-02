package com.learning.service;

import org.springframework.web.socket.WebSocketSession;

/**
 * ws 消息处理器
 * @author wangwei
 * @version 1.0
 * @date 2025/5/24 下午10:42
 */
public interface WsMsgHandler {

    boolean process(String message, WebSocketSession session);
}
