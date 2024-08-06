package com.learning.transaction.invoke;

import com.learning.transaction.handler.CallBackResultHandler;

import java.util.Map;

/**
 * @author WangWei
 * @version v 1.0
 * @description 消息发送
 * @date 2024-07-31
 **/
public class SyncInvoke {

    /**
     * 异步发送消息
     *
     * @param resultHandler 结果处理器
     * @param body 请求体
     * @param header 请求头
     */
    public <T> void syncInvoke(CallBackResultHandler<?> resultHandler, T body, Map<String, String> header) {

    }
}
