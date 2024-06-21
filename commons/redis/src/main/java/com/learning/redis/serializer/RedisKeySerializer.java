package com.learning.redis.serializer;

import com.learning.redis.consts.RedisToolsConstant;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author WangWei
 * @version v 1.0
 * @description key 序列化器
 * @date 2024-06-21
 **/
public class RedisKeySerializer implements RedisSerializer<Object> {
    /**
     * 编码
     */
    private final Charset charset;

    /**
     * 序列化转化器
     */
    private final ConversionService converter = DefaultConversionService.getSharedInstance();

    public RedisKeySerializer() {
        this(RedisToolsConstant.DEFAULT_CHARSET);
    }

    public RedisKeySerializer(Charset charset) {
        Objects.requireNonNull(charset, "Charset must not be null");
        this.charset = charset;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return bytes == null ? null : new String(bytes, this.charset);
    }

    @Override
    @Nullable
    public byte[] serialize(Object object) {
        Objects.requireNonNull(object, "redis key is null");
        return Objects.requireNonNull(
                object instanceof SimpleKey ? "" :
                        object instanceof String ? (String) object :
                                converter.convert(object, String.class)
        ).getBytes(charset);
    }
}
