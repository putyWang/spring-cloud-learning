package com.learning.file.storage.config;

import com.learning.core.utils.CollectionUtils;
import com.learning.file.storage.ProjectFileStorage;
import com.learning.file.storage.aspect.FileStorageAspect;
import com.learning.file.storage.config.properties.ProjectFileStorageProperties;
import com.learning.file.storage.service.ProjectFileRecorderService;
import com.learning.file.storage.service.ProjectFileStorageService;
import com.learning.file.storage.service.impl.DefaultProjectFileRecorderServiceImpl;
import com.learning.file.storage.service.impl.LocalProjectFileStorageService;
import com.learning.file.storage.service.impl.MinIOProjectFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableConfigurationProperties(ProjectFileStorageProperties.class)
@ConditionalOnMissingBean(ProjectFileStorage.class)
public class ProjectFileStorageConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectFileStorageProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 判断是否没有引入指定 Class
     */
    public static boolean doesNotExistClass(String name) {
        try {
            Class.forName(name);
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    /**
     * 配置本地存储的访问地址
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (ProjectFileStorageProperties.Local local : properties.getLocal()) {
            if (local.getEnableAccess()) {
                registry.addResourceHandler(local.getPathPatterns()).addResourceLocations("file:" + local.getBasePath());
            }
        }
    }

    /**
     * 本地存储 Bean
     */
    @Bean
    public List<LocalProjectFileStorageService> localFileStorageList() {
        return properties.getLocal().stream().map(local -> {
            if (!local.getEnableStorage()) return null;
            log.info("加载存储平台：{}", local.getPlatform());
            LocalProjectFileStorageService localFileStorage = new LocalProjectFileStorageService();
            localFileStorage.setPlatform(local.getPlatform());
            localFileStorage.setBasePath(local.getBasePath());
            localFileStorage.setDomain(local.getDomain());
            return localFileStorage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * MinIO 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "io.minio.MinioClient")
    public List<MinIOProjectFileStorageService> minioFileStorageList() {
        return properties.getMinio().stream().map(minio -> {
            if (!minio.getEnableStorage()) return null;
            log.info("加载存储平台：{}", minio.getPlatform());
            MinIOProjectFileStorageService storage = new MinIOProjectFileStorageService();
            storage.setPlatform(minio.getPlatform());
            storage.setAccessKey(minio.getAccessKey());
            storage.setSecretKey(minio.getSecretKey());
            storage.setEndPoint(minio.getEndPoint());
            storage.setBucketName(minio.getBucketName());
            storage.setDomain(minio.getDomain());
            storage.setBasePath(minio.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 当没有找到 FileRecorder 时使用默认的 FileRecorder
     */
    @Bean
    @ConditionalOnMissingBean(ProjectFileRecorderService.class)
    public ProjectFileRecorderService fileRecorder() {
        log.warn("没有找到 FileRecorder 的实现类，文件上传之外的部分功能无法正常使用，必须实现该接口才能使用完整功能！");
        return new DefaultProjectFileRecorderServiceImpl();
    }

    /**
     * 文件存储服务
     */
    @Bean(name = "ProjectFileStorageService")
    public ProjectFileStorage fileStorageService(ProjectFileRecorderService fileRecorder,
                                                 List<List<? extends ProjectFileStorageService>> fileStorageLists,
                                                 List<FileStorageAspect> aspectList) {
        this.initDetect();
        ProjectFileStorage service = new ProjectFileStorage();
        service.setFileStorageList(new CopyOnWriteArrayList<>());
        fileStorageLists.forEach(service.getFileStorageList()::addAll);
        service.setFileRecorder(fileRecorder);
        service.setProperties(properties);
        service.setAspectList(new CopyOnWriteArrayList<>(aspectList));
        return service;
    }

    /**
     * 对 FileStorageService 注入自己的代理对象，不然会导致针对 FileStorageService 的代理方法不生效
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshedEvent() {
        ProjectFileStorage service = applicationContext.getBean(ProjectFileStorage.class);
        service.setSelf(service);
    }

    public void initDetect() {
        String template = "检测到{}配置，但是没有找到对应的依赖库，所以无法加载此存储平台";

        if (CollectionUtils.isNotEmpty(properties.getMinio()) && doesNotExistClass("io.minio.MinioClient")) {
            log.warn(template, " MinIO ");
        }
    }

}
