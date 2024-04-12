package com.learning.file.storage.aspect;

import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.service.ProjectFileStorageService;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 下载切面调用链结束回调
 */
public interface DownloadAspectChainCallback {
    void run(FileInfo fileInfo, ProjectFileStorageService fileStorage, Consumer<InputStream> consumer);
}
