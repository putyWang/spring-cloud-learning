package com.learning.job.utils;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 手动服务发现工具类
 */
@Log4j2
public class DiscoveryUtil {
    private static boolean printRegistryLog = true;
    /**
     * 本地服务缓存
     */
    public static Map<String, List<String>> adminServicesList = new HashMap();
    private static NacosServiceDiscovery lookupService;

    public DiscoveryUtil(NacosServiceDiscovery lookupService) {
        DiscoveryUtil.lookupService = lookupService;
    }

    /**
     * 向缓存中添加服务
     *
     * @param name 服务名
     */
    public static void addList(String name) {
        List<String> serviceList = getServicesList(name);
        if (!CollectionUtils.isEmpty(serviceList)) {
            adminServicesList.put(name, serviceList);
            if (printRegistryLog) {
                log.info("registry scheduled success : {}", name);
                printRegistryLog = false;
            }
        }

    }

    /**
     * 获取指定服务 id 的服务信息
     *
     * @param name 服务名
     * @return
     */
    public static List<String> getServicesList(String name) {
        List<String> servicesList = new ArrayList();
        List<ServiceInstance> servicesByDiscovery = getServicesByDiscovery(name);
        if (!CollectionUtils.isEmpty(servicesByDiscovery)) {
            servicesByDiscovery.forEach((serviceInstance) -> {
                String service = serviceInstance.getHost() + ":" + serviceInstance.getPort();
                Map<String, String> metadata = serviceInstance.getMetadata();
                if (null != metadata) {
                    String contextPath = metadata.get("context-path");
                    if (!StringUtils.isEmpty(contextPath)) {
                        service = service + contextPath;
                    }
                }

                servicesList.add(service);
            });
        }

        return servicesList;
    }

    public static List<ServiceInstance> getServicesByDiscovery(String appName) {
        try {
            return lookupService.getInstances(appName);
        } catch (NacosException e) {
            log.error("service first discovery fail", e.toString());
            return null;
        }
    }

    public static boolean hostExist(String host) {

         for(String admin : adminServicesList.keySet()) {
            List<String> urls = adminServicesList.get(admin);
            for (String url : urls) {
                if (host.equals(url.split(":")[0])) {
                    return true;
                }
            }
        }

        return false;
    }
}

