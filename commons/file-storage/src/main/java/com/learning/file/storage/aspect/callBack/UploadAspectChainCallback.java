package com.learning.file.storage.aspect.callBack;


import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.model.UploadPretreatment;
import com.learning.file.storage.recorder.FileRecorder;
import com.learning.file.storage.storage.FileStorage;

/**
 * 上传切面调用链结束回调
 */
public interface UploadAspectChainCallback {
    FileInfo run(FileInfo fileInfo, UploadPretreatment pre, FileStorage fileStorage, FileRecorder fileRecorder);
}
