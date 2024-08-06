package com.learning.transaction.handler;

/**
 * @author WangWei
 * @version v 1.0
 * @description 外部系统调用结果处理
 * @date 2024-07-31
 **/
public interface CallBackResultHandler<T> {

    /**
     * 获取回调函数名
     * @return 回调函数名
     */
    String getName();

    /**
     * 操作成功执行
     * @param t 响应结果
     */
    void success(T t);

    /**
     * 操作失败执行
     * @param message 失败消息
     */
    void fail(String message);
}
