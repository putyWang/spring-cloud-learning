package com.learning.redis.serializer;

import com.learning.redis.consts.RedisToolsConstant;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author WangWei
 * @version v 1.0
 * @description redis 对象序列化类
 * @date 2024-06-21
 **/
public class RedisObjectSerializer implements RedisSerializer<Object> {

    /**
     * 序列化转换器
     */
    private final Converter<Object, byte[]> serializingConverter = new SerializingConverter();

    /**
     * 反序列化转换器
     */
    private final Converter<byte[], Object> deserializingConverter = new DeserializingConverter();

    @Override
    public byte[] serialize(Object obj) {
        return obj == null ? RedisToolsConstant.EMPTY_BYTE_ARRAY : this.serializingConverter.convert(obj);
    }

    @Override
    public Object deserialize(byte[] data) {
        return data != null && data.length != 0 ? this.deserializingConverter.convert(data) : null;
    }
}
