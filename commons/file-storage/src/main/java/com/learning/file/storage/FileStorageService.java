package com.learning.file.storage;

import cn.hutool.core.util.URLUtil;
import com.learning.core.utils.StringUtils;
import com.learning.file.storage.aspect.chain.DeleteAspectChain;
import com.learning.file.storage.aspect.chain.ExistsAspectChain;
import com.learning.file.storage.aspect.FileStorageAspect;
import com.learning.file.storage.aspect.chain.UploadAspectChain;
import com.learning.file.storage.config.properties.ProjectFileStorageProperties;
import com.learning.file.storage.exception.FileStorageException;
import com.learning.file.storage.model.*;
import com.learning.file.storage.recorder.FileRecorder;
import com.learning.file.storage.storage.FileStorage;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;


/**
 * 用来处理文件存储，对接多个平台
 * 对外暴露接口
 */
@Data
public class FileStorageService {

    private FileStorageService self;
    /**
     * 记录器
     */
    private FileRecorder fileRecorder;
    /**
     * 文件存储器列表
     */
    private CopyOnWriteArrayList<FileStorage> fileStorageList;
    /**
     * 文件存储属性
     */
    private ProjectFileStorageProperties properties;
    /**
     * 文件处理切面列表
     */
    private CopyOnWriteArrayList<FileStorageAspect> aspectList;

    /**
     * 获取默认的存储平台
     * @return
     */
    public FileStorage getFileStorage() {
        return getFileStorage(properties.getDefaultPlatform());
    }

    /**
     * 获取对应的存储平台，如果存储平台不存在则抛出异常
     * @param fileInfo 文件信息
     * @return
     */
    public FileStorage getFileStorageVerify(FileInfo fileInfo) {
        FileStorage fileStorageService = getFileStorage(fileInfo.getPlatform());
        if (fileStorageService == null) throw new FileStorageException("没有找到对应的存储平台！");
        return fileStorageService;
    }

    /**
     * 获取对应的存储平台,如果存储平台不存在则返回默认的存储平台
     */
    public FileStorage getFileStorage(String platform) {
        FileStorage cur = null;
        FileStorage defaultStorage = null;

        for (FileStorage fileStorageService : fileStorageList) {

            if (StringUtils.equals(properties.getDefaultPlatform(), fileStorageService.getPlatform())) {
                defaultStorage = fileStorageService;
            }

            if (StringUtils.equals(platform, fileStorageService.getPlatform())) {
                cur = fileStorageService;
                break;
            }
        }

        if (cur == null) {
            // 获取默认平台
            cur = defaultStorage;
        }

        return cur;
    }

    /**
     * 上传文件
     * @param pre 文件上传预处理对象
     * @return
     */
    public FileInfo upload(UploadPretreatment pre) {
        // 1.获取对应存储平台
        FileStorage fileStorageService = getFileStorage(pre.getPlatform());
        if (fileStorageService == null) throw new FileStorageException("没有找到对应的存储平台！");

        // 2.处理切面
        return new UploadAspectChain(aspectList, (_fileInfo, _pre, _fileStorage, _fileRecorder) -> {
            // 3.上传文件同时记录上传信息
            if (_fileStorage.upload(_fileInfo, _pre)) {
                if (_fileRecorder.record(_fileInfo)) {
                    return _fileInfo;
                }
            }
            return null;
        }).next(FileInfo.buildByUploadPretreatment(pre), pre, fileStorageService, fileRecorder);
    }

    /**
     * 根据 url 获取 FileInfo
     * @param url url 信息
     * @return
     */
    public FileInfo getFileInfoByUrl(String url) {
        return fileRecorder.getByUrl(url);
    }

    /**
     * 根据 url 删除文件
     * @param url url 信息
     * @return
     */
    public boolean delete(String url) {
        return delete(getFileInfoByUrl(url));
    }

    /**
     * 根据 url 删除文件
     * @param url url 信息
     * @param predicate 删除条件断言
     * @return
     */
    public boolean delete(String url, Predicate<FileInfo> predicate) {
        return delete(getFileInfoByUrl(url), predicate);
    }

    /**
     * 根据文件信息删除文件
     * @param fileInfo 文件信息
     * @return
     */
    public boolean delete(FileInfo fileInfo) {
        return delete(fileInfo, null);
    }

    /**
     * 根据 文件信息 有条件的删除文件
     * @param fileInfo 文件信息
     * @param predicate 删除条件断言
     * @return
     */
    public boolean delete(FileInfo fileInfo, Predicate<FileInfo> predicate) {
        if (fileInfo == null) return true;
        if (predicate != null && !predicate.test(fileInfo)) return false;
        FileStorage fileStorage = getFileStorage(fileInfo.getPlatform());
        if (fileStorage == null) throw new FileStorageException("没有找到对应的存储平台！");

        return new DeleteAspectChain(aspectList, (_fileInfo, _fileStorage, _fileRecorder) -> {
            if (_fileStorage.delete(_fileInfo)) {   //删除文件
                return _fileRecorder.delete(_fileInfo.getUrl());  //删除文件记录
            }
            return false;
        }).next(fileInfo, fileStorage, fileRecorder);
    }

    /**
     * 指定 url 文件是否存在
     * @param url 文件 url
     * @return
     */
    public boolean exists(String url) {
        return exists(getFileInfoByUrl(url));
    }

    /**
     * 是否存在指定文件信息
     * @param fileInfo 文件信息
     * @return
     */
    public boolean exists(FileInfo fileInfo) {
        if (fileInfo == null) return false;
        return new ExistsAspectChain(aspectList, (_fileInfo, _fileStorage) ->
                _fileStorage.exists(_fileInfo)
        ).next(fileInfo, getFileStorageVerify(fileInfo));
    }

    /**
     * 获取指定文件关联下载器
     * @param fileInfo 文件信息
     * @return
     */
    public Downloader download(FileInfo fileInfo) {
        return new Downloader(fileInfo, aspectList, getFileStorageVerify(fileInfo), Downloader.TARGET_FILE);
    }

    /**
     * 获取文件下载器
     */
    public Downloader download(String url) {
        return download(getFileInfoByUrl(url));
    }

    /**
     * 获取缩略图文件下载器
     */
    public Downloader downloadTh(FileInfo fileInfo) {
        return new Downloader(fileInfo, aspectList, getFileStorageVerify(fileInfo), Downloader.TARGET_TH_FILE);
    }

    /**
     * 获取缩略图文件下载器
     */
    public Downloader downloadTh(String url) {
        return downloadTh(getFileInfoByUrl(url));
    }

    /**
     * 创建上传预处理器
     */
    public UploadPretreatment of() {
        UploadPretreatment pre = new UploadPretreatment();
        pre.setProjectFileStorage(self);
        pre.setPlatform(properties.getDefaultPlatform());
        pre.setThumbnailSuffix(properties.getThumbnailSuffix());
        return pre;
    }

    /**
     * 根据 MultipartFile 创建上传预处理器
     */
    public UploadPretreatment of(MultipartFile file) {
        UploadPretreatment pre = of();
        pre.setFileWrapper(new MultipartFileWrapper(file));
        return pre;
    }

    /**
     * 根据 byte[] 创建上传预处理器，name 为空字符串
     */
    public UploadPretreatment of(byte[] bytes) {
        UploadPretreatment pre = of();
        pre.setFileWrapper(new MultipartFileWrapper(new MockMultipartFile("", bytes)));
        return pre;
    }

    /**
     * 根据 InputStream 创建上传预处理器，originalFilename 为空字符串
     */
    public UploadPretreatment of(InputStream in) {
        try {
            UploadPretreatment pre = of();
            pre.setFileWrapper(new MultipartFileWrapper(new MockMultipartFile("", in)));
            return pre;
        } catch (Exception e) {
            throw new FileStorageException("根据 InputStream 创建上传预处理器失败！", e);
        }
    }

    /**
     * 根据 File 创建上传预处理器，originalFilename 为 file 的 name
     */
    public UploadPretreatment of(File file) {
        try {
            UploadPretreatment pre = of();
            pre.setFileWrapper(new MultipartFileWrapper(new MockMultipartFile(file.getName(), file.getName(), null, Files.newInputStream(file.toPath()))));
            return pre;
        } catch (Exception e) {
            throw new FileStorageException("根据 File 创建上传预处理器失败！", e);
        }
    }

    /**
     * 根据 URL 创建上传预处理器，originalFilename 为空字符串
     * @param url url 对象
     * @return
     */
    public UploadPretreatment of(URL url) {
        try {
            UploadPretreatment pre = of();
            pre.setFileWrapper(new MultipartFileWrapper(new MockMultipartFile("", url.openStream())));
            return pre;
        } catch (Exception e) {
            throw new FileStorageException("根据 URL 创建上传预处理器失败！", e);
        }
    }

    /**
     * 根据 URI 创建上传预处理器，originalFilename 为空字符串
     * @param uri uri 对象
     * @return
     */
    public UploadPretreatment of(URI uri) {
        try {
            return of(uri.toURL());
        } catch (Exception e) {
            throw new FileStorageException("根据 URI 创建上传预处理器失败！", e);
        }
    }

    /**
     * 根据 url 字符串创建上传预处理器，兼容Spring的ClassPath路径、文件路径、HTTP路径等，originalFilename 为空字符串
     */
    public UploadPretreatment of(String url) {
        try {
            return of(URLUtil.url(url));
        } catch (Exception e) {
            throw new FileStorageException("根据 url：" + url + " 创建上传预处理器失败！", e);
        }
    }

}
