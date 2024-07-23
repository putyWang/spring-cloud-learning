package com.learning.redis.consts;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author WangWei
 * @description
 * @date 2024-06-21
 **/
public interface RedisToolsConstant {

    /**
     * 空白字节数组
     */
    byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * 默认编码方式
     */
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
}