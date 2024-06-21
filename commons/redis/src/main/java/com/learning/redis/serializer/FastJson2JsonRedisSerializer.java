package com.learning.redis.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.learning.redis.consts.RedisToolsConstant;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author WangWei
 * @version v 1.0
 * @description json 序列化对象
 * @date 2024-06-21
 **/
@AllArgsConstructor
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

    /**
     * 目标序列化类
     */
    private final Class<T> clazz;

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return t == null ? RedisToolsConstant.EMPTY_BYTE_ARRAY :
                JSON.toJSONString(t, new SerializerFeature[]{SerializerFeature.WriteClassName})
                        .getBytes(RedisToolsConstant.DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null && bytes.length > 0) {
            return JSON.parseObject(new String(bytes, RedisToolsConstant.DEFAULT_CHARSET), clazz);
        } else {
            return null;
        }
    }
}
