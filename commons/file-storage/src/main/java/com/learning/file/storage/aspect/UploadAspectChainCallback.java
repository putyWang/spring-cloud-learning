package com.learning.file.storage.aspect;


import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.model.UploadPretreatment;
import com.learning.file.storage.service.ProjectFileRecorderService;
import com.learning.file.storage.service.ProjectFileStorageService;

/**
 * 上传切面调用链结束回调
 */
public interface UploadAspectChainCallback {
    FileInfo run(FileInfo fileInfo, UploadPretreatment pre, ProjectFileStorageService fileStorage, ProjectFileRecorderService fileRecorder);
}
