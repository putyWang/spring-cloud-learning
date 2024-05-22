package com.learning.Job.schedule.core.utils;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class I18nUtil {
    private static Logger logger = LoggerFactory.getLogger(I18nUtil.class);
    private static Properties prop = null;

    public I18nUtil() {
    }

    public static Properties loadI18nProp() {
        if (prop != null) {
            return prop;
        } else {
            try {
                String i18n = XxlJobAdminConfig.getAdminConfig().getI18n();
                i18n = i18n != null && i18n.trim().length() > 0 ? "_" + i18n : i18n;
                String i18nFile = MessageFormat.format("i18n/message{0}.properties", i18n);
                Resource resource = new ClassPathResource(i18nFile);
                EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
                prop = PropertiesLoaderUtils.loadProperties(encodedResource);
            } catch (IOException var4) {
                IOException e = var4;
                logger.error(e.getMessage(), e);
            }

            return prop;
        }
    }

    public static String getString(String key) {
        return loadI18nProp().getProperty(key);
    }

    public static String getMultString(String... keys) {
        Map<String, String> map = new HashMap();
        Properties prop = loadI18nProp();
        if (keys != null && keys.length > 0) {
            String[] var7 = keys;
            int var9 = keys.length;

            for(int var5 = 0; var5 < var9; ++var5) {
                String key = var7[var5];
                map.put(key, prop.getProperty(key));
            }
        } else {
            Iterator var3 = prop.stringPropertyNames().iterator();

            while(var3.hasNext()) {
                String key = (String)var3.next();
                map.put(key, prop.getProperty(key));
            }
        }

        String json = JacksonUtil.writeValueAsString(map);
        return json;
    }
}

