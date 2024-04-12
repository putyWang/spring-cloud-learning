package com.learning.file.storage.aspect;

import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.service.ProjectFileStorageService;

/**
 * 文件是否存在切面调用链结束回调
 */
public interface ExistsAspectChainCallback {
    boolean run(FileInfo fileInfo, ProjectFileStorageService fileStorage);
}
