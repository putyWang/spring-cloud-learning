package com.learning.file.storage.aspect;


import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.service.ProjectFileStorageService;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 下载缩略图切面调用链结束回调
 */
public interface DownloadThAspectChainCallback {
    void run(FileInfo fileInfo, ProjectFileStorageService fileStorage, Consumer<InputStream> consumer);
}
