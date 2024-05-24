package com.learning.job.utils;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.LookupService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class DiscoveryUtil {
    private static boolean printRegistryLog = true;
    private static Logger logger = LoggerFactory.getLogger(DiscoveryUtil.class);
    public static Map<String, List<String>> adminServicesList = new HashMap();
    private static LookupService<InstanceInfo> lookupService;

    public DiscoveryUtil(LookupService<InstanceInfo> lookupService) {
        DiscoveryUtil.lookupService = lookupService;
    }

    public static void addList(String name) {
        List<InstanceInfo> servicesByDiscovery = getServicesByDiscovery(name);
        if (!CollectionUtils.isEmpty(servicesByDiscovery)) {
            List<String> serviceList = new ArrayList();
            servicesByDiscovery.forEach((instanceInfo) -> {
                String service = instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();
                Map<String, String> metadata = instanceInfo.getMetadata();
                if (null != metadata) {
                    String contextPath = (String)metadata.get("context-path");
                    if (!StringUtils.isEmpty(contextPath)) {
                        service = service + contextPath;
                    }
                }

                serviceList.add(service);
            });
            adminServicesList.put(name, serviceList);
            if (printRegistryLog) {
                logger.info("registry scheduled success : {}", name);
                printRegistryLog = false;
            }
        }

    }

    public static List<String> getServicesList(String name) {
        List<String> servicesList = new ArrayList();
        List<InstanceInfo> servicesByDiscovery = getServicesByDiscovery(name);
        if (!CollectionUtils.isEmpty(servicesByDiscovery)) {
            servicesByDiscovery.forEach((instanceInfo) -> {
                String service = instanceInfo.getIPAddr() + ":" + instanceInfo.getPort();
                Map<String, String> metadata = instanceInfo.getMetadata();
                if (null != metadata) {
                    String contextPath = (String)metadata.get("context-path");
                    if (!StringUtils.isEmpty(contextPath)) {
                        service = service + contextPath;
                    }
                }

                servicesList.add(service);
            });
        }

        return servicesList;
    }

    public static List<InstanceInfo> getServicesByDiscovery(String appName) {
        Application application = null;

        try {
            application = lookupService.getApplication(appName);
        } catch (NullPointerException var3) {
            NullPointerException e = var3;
            logger.error("service first discovery fail : {}", e.toString());
        }

        List<InstanceInfo> instances = null;
        if (application != null) {
            instances = application.getInstances();
        } else {
            logger.error(" -> {}", appName);
        }

        return instances;
    }

    public static boolean hostExist(String host) {
        Iterator var1 = adminServicesList.keySet().iterator();

        ArrayList ipList;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            String admin = (String)var1.next();
            List<String> urls = (List)adminServicesList.get(admin);
            ipList = new ArrayList(3);
            Iterator var5 = urls.iterator();

            while(var5.hasNext()) {
                String url = (String)var5.next();
                String[] split = url.split(":");
                ipList.add(split[0]);
            }
        } while(!ipList.contains(host));

        return true;
    }
}

