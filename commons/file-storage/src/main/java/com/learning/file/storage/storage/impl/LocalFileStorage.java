package com.learning.file.storage.storage.impl;

import cn.hutool.core.io.FileUtil;
import com.learning.file.storage.exception.FileStorageException;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地文件存储
 */
@Getter
@Setter
public class LocalFileStorage extends FileStorage {

    @Override
    public boolean uploadFile(String newFileKey, InputStream inputStream) {
        try {
            FileUtils.copyToFile(inputStream, FileUtil.touch(newFileKey));
            return true;
        } catch (IOException e) {
            throw new FileStorageException("文件上传失败！platform：" + this.getPlatform() + "，filename：" + newFileKey, e);
        }
    }

    @Override
    public boolean deleteFile(String fileKey) {
        return FileUtil.del(new File(fileKey));
    }


    @Override
    public boolean fileExists(String fileKey) {
        return new File(fileKey).exists();
    }

    @Override
    public InputStream downloadFile(String fileKey) {
        return FileUtil.getInputStream(fileKey);
    }

    @Override
    public void updateUrl(FileInfo fileInfo) {
        String domain = this.getDomain();
        fileInfo.setUrl(domain + fileInfo.getPath() + fileInfo.getFilename());
        fileInfo.setThUrl(domain + fileInfo.getPath() + fileInfo.getThFilename());
    }
}
