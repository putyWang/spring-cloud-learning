package com.learning.file.storage.config;

import com.learning.core.utils.CollectionUtils;
import com.learning.file.storage.FileStorageService;
import com.learning.file.storage.aspect.FileStorageAspect;
import com.learning.file.storage.config.properties.ProjectFileStorageProperties;
import com.learning.file.storage.recorder.FileRecorder;
import com.learning.file.storage.storage.FileStorage;
import com.learning.file.storage.recorder.impl.DefaultFileRecorder;
import com.learning.file.storage.storage.impl.LocalFileStorage;
import com.learning.file.storage.storage.impl.MinIOFileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableConfigurationProperties(ProjectFileStorageProperties.class)
@ConditionalOnMissingBean(FileStorageService.class)
@RequiredArgsConstructor
public class ProjectFileStorageConfig {

    private final ProjectFileStorageProperties properties;

    private final ApplicationContext applicationContext;

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
     * 本地存储 Bean
     */
    @Bean
    public List<LocalFileStorage> localFileStorageList() {
        return properties.getLocal().stream().map(local -> {
            if (!local.getEnableStorage()) return null;
            log.info("加载存储平台：{}", local.getPlatform());
            LocalFileStorage localFileStorage = new LocalFileStorage();
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
    public List<MinIOFileStorage> minioFileStorageList() {
        return properties.getMinio().stream().map(minio -> {
            if (! minio.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", minio.getPlatform());
            MinIOFileStorage storage = new MinIOFileStorage(minio.getAccessKey(), minio.getSecretKey(), minio.getEndPoint(), minio.getBucketName());
            storage.setPlatform(minio.getPlatform());
            storage.setDomain(minio.getDomain());
            storage.setBasePath(minio.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 当没有找到 FileRecorder 时使用默认的 FileRecorder
     */
    @Bean
    @ConditionalOnMissingBean(FileRecorder.class)
    public FileRecorder fileRecorder() {
        log.warn("没有找到 FileRecorder 的实现类，文件上传之外的部分功能无法正常使用，必须实现该接口才能使用完整功能！");
        return new DefaultFileRecorder();
    }

    /**
     * 文件存储服务
     */
    @Bean(name = "ProjectFileStorageService")
    public FileStorageService fileStorageService(FileRecorder fileRecorder,
                                                 List<? extends FileStorage> fileStorageList,
                                                 List<FileStorageAspect> aspectList) {
        this.initDetect();
        return new FileStorageService()
                .setFileStorageList(new CopyOnWriteArrayList<>(fileStorageList))
                .setFileRecorder(fileRecorder)
                .setProperties(properties)
                .setAspectList(new CopyOnWriteArrayList<>(aspectList));
    }

    /**
     * 对 FileStorageService 注入自己的代理对象，不然会导致针对 FileStorageService 的代理方法不生效
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshedEvent() {
        FileStorageService service = applicationContext.getBean(FileStorageService.class);
        service.setSelf(service);
    }

    public void initDetect() {
        String template = "检测到{}配置，但是没有找到对应的依赖库，所以无法加载此存储平台";

        if (CollectionUtils.isNotEmpty(properties.getMinio()) && doesNotExistClass("io.minio.MinioClient")) {
            log.warn(template, " MinIO ");
        }
    }

}
