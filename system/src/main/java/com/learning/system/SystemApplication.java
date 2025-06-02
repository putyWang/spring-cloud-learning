package com.learning.system;

import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackages = {"com.learning.*"})
@EnableDiscoveryClient
@EnableFeignClients
//@EnableMethodCache(basePackages = "com.learning.gateway")
//@EnableCreateCacheAnnotation
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
