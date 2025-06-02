package com.learning.config.ws.model;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * WsSession 相关参数存储 器
 * @author wangwei
 * @version 1.0
 * @date 2025/5/24 下午9:32
 */
public class WsSessionHolder {

    private WsSessionHolder(){}

    private static final Map<String, Set<String>> SESSION_KEY_ID_MAP = new ConcurrentHashMap<>();

    private static final Map<String, WebSocketSession> SESSION_CACHE = new ConcurrentHashMap<>();

    private static final ThreadLocal<QueryModel> QUERY_MODEL_THREAD_LOCAL = new ThreadLocal<>();

    private static final Logger log = LoggerFactory.getLogger(WsSessionHolder.class);

    private static final Map<String, Long> LAST_ACTIVE_TIME = new ConcurrentHashMap<>();

    /**
     * 保存 session 对象
     * @param session session 对象
     */
    public static void putSession(WebSocketSession session) {
        if(! SESSION_CACHE.containsKey(session.getId())) {
            SESSION_CACHE.put(session.getId(), session);

            SESSION_KEY_ID_MAP.computeIfAbsent(
                    cacheKeyBuild(session, true),
                    key -> new CopyOnWriteArraySet<>()
            ).add(session.getId());

            LAST_ACTIVE_TIME.put(session.getId(), System.currentTimeMillis());
        }
    }

    /**
     * 移除 session 对象
     * @param session session 对象
     */
    public static void removeSession(WebSocketSession session) {
        if (SESSION_CACHE.containsKey(session.getId())) {
            SESSION_CACHE.remove(session.getId());

            SESSION_KEY_ID_MAP.computeIfAbsent(
                    cacheKeyBuild(session, false),
                    key -> new CopyOnWriteArraySet<>()
            ).remove(session.getId());

            LAST_ACTIVE_TIME.remove(session.getId());
        }
    }

    /**
     * 发送消息
     * @param msg 消息值
     */
    public static void sendMsg(String msg) {
        // 1 获取当前线程查询参数
        QueryModel queryModel = QUERY_MODEL_THREAD_LOCAL.get();
        Assert.notNull(queryModel, "当前线程中不存在查询参数对象");
        // 2 发送消息
        sendMsg(queryModel, null, msg);
    }

    /**
     * 发送消息
     * @param sessionId 会话 id
     * @param msg 消息值
     */
    public static void sendMsg(String sessionId, String msg) {
        // 1 获取当前线程查询参数
        QueryModel queryModel = QUERY_MODEL_THREAD_LOCAL.get();
        Assert.notNull(queryModel, "当前线程中不存在查询参数对象");
        // 2 发送消息
        sendMsg(queryModel, sessionId, msg);
    }

    /**
     * 发送消息
     * @param queryModel 链接 url 查询参数
     * @param msg 消息值
     */
    public static void sendMsg(QueryModel queryModel, String msg) {
        sendMsg(queryModel, null, msg);
    }

    /**
     * 发送消息
     * @param queryModel 链接 url 查询参数
     * @param sessionId 会话 id
     * @param msg 消息值
     */
    public static void sendMsg(QueryModel queryModel, String sessionId, String msg) {
        List<WebSocketSession> sessionList = new ArrayList<>();
        // sessionId 不为空直接向对应会话中发送消息
        if (StrUtil.isNotEmpty(sessionId)) {
            if (SESSION_CACHE.containsKey(sessionId)) {
                sessionList = Collections.singletonList(SESSION_CACHE.get(sessionId));
            }
        // 为空时需计算需发送的会话列表
        } else if (ObjectUtil.isNotNull(queryModel)) {
            Set<String> sessionIdList = SESSION_KEY_ID_MAP.get(queryModel.getKey());

            if (CollUtil.isEmpty(sessionIdList)) {
                SESSION_KEY_ID_MAP.remove(queryModel.getKey());
            } else {
                sessionList = new ArrayList<>();

                for (String sessionNewId : sessionIdList) {
                    sessionList.add(SESSION_CACHE.get(sessionNewId));
                }
            }
        }
        // 3 遍历发送消息
        if (CollUtil.isEmpty(sessionList)) {
            return;
        }
        sessionList.forEach(
                session -> {
                    try {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(msg));
                        } else {
                            removeSession(session);
                        }
                    } catch (IOException e) {
                        log.error("{}信息发送失败", msg, e);
                    }
                }
        );
    }

    /**
     * 上次活跃时间更新
     * @param sessionId 链接 url 查询参数
     */
    public static void updateActiveTime(String sessionId) {
        LAST_ACTIVE_TIME.put(sessionId, System.currentTimeMillis());
    }

    /**
     * 为当前线程设置 QueryModel 对象
     * @param session 会话对象
     * @return QueryModel 对象
     */
    public static QueryModel setQueryModel(WebSocketSession session) {
        // 利用框架解析 url 参数，获取参数映射
        Map<String, String> queryMap = UriComponentsBuilder.fromUri(session.getUri())
                .build().getQueryParams()
                .toSingleValueMap();
        // 利用 queryMap 生产 QueryModel 对象
        QueryModel queryModel = QueryModel.newBuilder()
                .queryParam(queryMap).build();
        QUERY_MODEL_THREAD_LOCAL.set(queryModel);
        return queryModel;
    }

    /**
     * 会话空闲测试
     * @param timeOut 超时值
     * @param heartBeatTime 心跳检测时间
     */
    public static void idleCheck(Long timeOut, Long heartBeatTime) {
        Iterator<Map.Entry<String, Long>> activeSessionIterator = LAST_ACTIVE_TIME.entrySet().iterator();
        while (activeSessionIterator.hasNext()) {
            Map.Entry<String, Long> activeSession = activeSessionIterator.next();
            String sessionId = activeSession.getKey();
            long activeTime = activeSession.getValue();

            if (! SESSION_CACHE.containsKey(sessionId)) {
                activeSessionIterator.remove();
                return;
            }
            WebSocketSession session = SESSION_CACHE.get(sessionId);

            if (! session.isOpen()) {
                SESSION_CACHE.remove(sessionId);
                activeSessionIterator.remove();
                return;
            }

            long idleTime = (System.currentTimeMillis() - activeTime)/1000;

            try {
                if (idleTime < heartBeatTime) {
                    return;
                } else if (idleTime < timeOut) {
                    session.sendMessage(new PingMessage());
                } else {
                    log.error("{} 会话已超时，将会关闭", sessionId);
                    session.close();
                }
            } catch (IOException e) {
                log.error("{} 消息发送失败", sessionId, e);
            }

        }
    }

    /**
     * 缓存 key 构建
     * @param session session 对象
     * @return 缓存 key
     */
    private static String cacheKeyBuild(WebSocketSession session, boolean add) {
        QueryModel queryModel;
        if (add || ObjectUtil.isNull(QUERY_MODEL_THREAD_LOCAL.get())) {
            queryModel = setQueryModel(session);
        } else {
            queryModel = QUERY_MODEL_THREAD_LOCAL.get();
        }

        if (! add) {
            QUERY_MODEL_THREAD_LOCAL.remove();
        }
        return queryModel.getKey();
    }
}
