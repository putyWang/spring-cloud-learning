package com.learning.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 日志记录相关配置参数
 */
@Data
@RefreshScope
@Component
@ConfigurationProperties(prefix = "gateway.log.access")
public class LogProperties {

    /**
     * 是否开启日志打印
     */
    private Boolean enabled = true;

    /**
     * 匹配的pattern
     */
    private String matchUrl = "";

    private ApiAlarmConfiguration fail = new ApiAlarmConfiguration();

    private SlowApiAlarmConfiguration slow = new SlowApiAlarmConfiguration();

    /**
     * 慢API报警配置
     */
    @Data
    public static class SlowApiAlarmConfiguration {

        /**
         * 是否开启API慢日志打印
         */
        private boolean alarm = true;

        /**
         * 报警阈值 （单位：毫秒）
         */
        private long threshold = 500;
    }


    /**
     * API异常报警(根据http状态码判定）
     */
    @Data
    public static class ApiAlarmConfiguration {

        /**
         * 是否开启异常报警 默认关闭
         */
        private boolean alarm = false;

        /**
         * 排除状态码
         */
        private List<Integer> exclusion;
    }
}
