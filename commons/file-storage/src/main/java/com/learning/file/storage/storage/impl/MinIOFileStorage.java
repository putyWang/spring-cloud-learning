package com.learning.file.storage.storage.impl;

import com.learning.core.utils.HttpUtils;
import com.learning.core.utils.StringUtil;
import com.learning.file.storage.exception.FileStorageException;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaTypeFactory;

import java.io.InputStream;

/**
 * MinIO 存储
 */
@Log4j2
public class MinIOFileStorage extends FileStorage {

    private MinioClient client;

    private String bucketName;

    public MinIOFileStorage(String accessKey, String secretKey, String endPoint, String bucketName) {
        try {
            this.bucketName = bucketName;
            // 1.获取 minio 链接
            MinioClient client = MinioClient.builder()
                    .endpoint(endPoint)
                    .credentials(accessKey, secretKey)
                    .httpClient(HttpUtils.getUnsafeOkHttpsClient())
                    .build();
            // 2.创建桶
            if (! client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build())) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
            this.client = client;
        } catch (Exception e) {
            throw new FileStorageException("minioClient创建失败", e);
        }
    }

    @Override
    public boolean uploadFile(String newFileKey, InputStream inputStream) throws Exception {
        client.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(MediaTypeFactory.getMediaType(newFileKey).get().getType())
                        .object(newFileKey)
                        .bucket(bucketName)
                        .build()
        );
        return true;
    }

    @Override
    public boolean deleteFile(String fileKey) throws Exception {
        client.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileKey)
                        .build()
        );

        return true;
    }

    @Override
    public boolean fileExists(String fileKey) throws Exception {
        Iterable<Result<Item>> myObjects = client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        for (Result<Item> result : myObjects) {
            if (fileKey.equals(result.get().objectName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public InputStream downloadFile(String fileName) throws Exception {
        return client.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }

    @Override
    public void updateUrl(FileInfo fileInfo) {
        try {
            fileInfo.setUrl(client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileInfo.getFilename())
                    .build())
            );
            if(StringUtil.isNoneBlank(fileInfo.getThFilename())) {
                fileInfo.setThUrl(client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileInfo.getThFilename())
                        .build())
                );
            }
        } catch (Exception e) {
            log.error("文件访问地址设置有误", e);
        }
    }
}
