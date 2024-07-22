package com.learning.redis.config.properties;

import com.learning.redis.consts.enums.RedisModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WangWei
 * @version v 1.0
 * @description 基础 redis 配置类
 * @date 2024-07-22
 **/
@Component
@ConfigurationProperties(prefix = "learning.cloud.redis")
@Data
public class RedisProperties {

    /**
     * 数据库编码
     */
    private int database = 0;

    /**
     * redis 链接模式
     * SINGLE-单机
     * CLUSTERS-集群
     * SENTINEL-哨兵
     */
    private RedisModel mode = RedisModel.SINGLE;
    /**
     * 主机地址
     */
    private String host = null;
    /**
     * 端口地址
     */
    private String port = null;
    /**
     * 超时时间
     */
    private int timeout = 3000;

    /**
     * 密码
     */
    private String password = null;

    /**
     * 是否在无法连接是抛出异常
     */
    private boolean throwErr = true;

    /**
     * sentinel 配置
     */
    private Sentinel sentinel = new Sentinel();

    /**
     * lettuce 配置
     */
    private Lettuce lettuce = new Lettuce();

    /**
     *
     */
    @Data
    public static class Sentinel{
        /**
         *
         */
        private String nodes = null;
        private String master = null;
    }

    /**
     * lettuce 配置
     */
    @Data
    public static class Lettuce{

        /**
         * 最小主节点个数
         */
        private int masterMinSize = 10;

        /**
         * 最小从节点数
         */
        private int slaveMinSize = 10;

        /**
         * 链接池配置
         */
        private LettucePool pool = new LettucePool();
    }

    /**
     * lettuce 连接池配置
     */
    @Data
    public static class LettucePool{
        /**
         * 超时关闭时间
         */
        private int shutdownTimeout = 100;

        /**
         * 最大活动链接数
         */
        private int maxActive = 200;

        /**
         * 最大空闲链接数
         */
        private int maxIdle = 10;

        /**
         * 等待队列最大数
         */
        private int maxWait = 10000;

        /**
         * 最小空闲链接数
         */
        private int minIdle = 5;
    }
}
