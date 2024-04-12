package com.learning.file.storage.storage;

import com.learning.core.utils.StringUtils;
import com.learning.file.storage.exception.FileStorageException;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.model.UploadPretreatment;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 文件存储接口，对应各个平台
 */
@Log4j2
@Data
public abstract class FileStorage {

    /**
     * 存储根路径
     */
    private String basePath;

    /**
     * 存储平台
     */
    private String platform;

    /**
     * 访问域名
     */
    private String domain;

    /**
     * 文件上传
     * @param fileInfo 文件信息
     * @param pre 文件上传流
     * @return
     */
    public boolean upload(FileInfo fileInfo, UploadPretreatment pre) {
        try{
            // 1.设置上传基本信息
            updateInfo(fileInfo);
            // 2.上传完整文件
            String newFileKey = fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename();

            if(uploadFile(newFileKey, pre.getFileWrapper().getMultipartFile().getInputStream())) {
                // 3.上传缩略图
                byte[] thumbnailBytes = pre.getThumbnailBytes();

                if (thumbnailBytes != null) {
                    String newThFileKey = fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename();

                    return uploadFile(newThFileKey, new ByteArrayInputStream(thumbnailBytes));
                }else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("上传{}文件失败", fileInfo.getFilename());
            return false;
        }
    }

    /**
     * 完整文件上传
     * @param newFileKey
     * @param inputStream
     * @return
     */
    public abstract boolean uploadFile(String newFileKey, InputStream inputStream) throws Exception;

    /**
     * 删除文件
     * @param fileInfo
     * @return
     */
    public boolean delete(FileInfo fileInfo) {
        try {
            updateInfo(fileInfo);
            if (fileInfo.getThFilename() != null) {   //删除缩略图
                deleteFile(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
            }
            return deleteFile(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        }catch (Exception e) {
            throw new FileStorageException("文件删除失败！platform：" + fileInfo, e);
        }
    }

    /**
     * 删除文件
     * @param fileKey
     * @return
     */
    public abstract boolean deleteFile(String fileKey) throws Exception;

    /**
     * 文件是否存在
     * @param fileInfo
     * @return
     */
    public boolean exists(FileInfo fileInfo) {
        updateInfo(fileInfo);
        try {
            return fileExists(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     * @param fileKey
     * @return
     */
    public abstract boolean fileExists(String fileKey) throws Exception;

    /**
     * 下载文件
     * @param fileInfo
     * @return
     */
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {
        updateInfo(fileInfo);
        try (InputStream in = downloadFile(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename())) {
            consumer.accept(in);
        } catch (Exception e) {
            throw new FileStorageException("文件下载失败！platform：" + fileInfo, e);
        }
    }

    /**
     * 下载缩略图
     * @param fileInfo
     * @return
     */
    public void downloadTh(FileInfo fileInfo, Consumer<InputStream> consumer) {
        updateInfo(fileInfo);
        if (StringUtils.isBlank(fileInfo.getThFilename())) {
            throw new FileStorageException("缩略图文件下载失败，文件不存在！fileInfo：" + fileInfo);
        }
        try (InputStream in = downloadFile(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename())) {
            consumer.accept(in);
        } catch (Exception e) {
            throw new FileStorageException("缩略图文件下载失败！fileInfo：" + fileInfo, e);
        }
    }

    /**
     * 下载文件
     * @param fileKey
     * @return
     */
    public abstract InputStream downloadFile(String fileKey) throws Exception;

    /**
     * 根据文件信息设置基础信息
     * @param fileInfo
     */
    public void updateInfo(FileInfo fileInfo) {
        // 1.将 path 路径设置为文件夹路径
        String path = fileInfo.getPath();

        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        fileInfo.setPath(path);
        // 2.设置根路径
        fileInfo.setBasePath(basePath);
    }

    /**
     * 更新 url 参数
     * @param fileInfo
     * @return
     */
    public abstract void updateUrl(FileInfo fileInfo);
}
