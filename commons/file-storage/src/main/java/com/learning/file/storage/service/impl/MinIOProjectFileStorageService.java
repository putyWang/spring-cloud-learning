package com.learning.file.storage.service.impl;

import com.learning.core.utils.HttpUtils;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.service.ProjectFileStorageService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaTypeFactory;

import java.io.InputStream;

/**
 * MinIO 存储
 */
@Getter
@Setter
public class MinIOProjectFileStorageService extends ProjectFileStorageService {

    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;

    private MinioClient client;

    public MinIOProjectFileStorageService (String accessKey, String secretKey, String endPoint, String bucketName) {
        this.bucketName = bucketName;
        try {
            // 1.获取 minio 链接
            client = MinioClient.builder()
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
        } catch (Exception e) {
            e.printStackTrace();
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
    public InputStream downloadFile(String fileKey) throws Exception {
        return client.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileKey)
                        .build());
    }

    @Override
    public void updateUrl(FileInfo fileInfo) {
        fileInfo.setUrl(this.getDomain() + fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        fileInfo.setThUrl(this.getDomain() + fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
    }
}
