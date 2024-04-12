package com.learning.file.storage.aspect.callBack;

import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 下载切面调用链结束回调
 */
public interface DownloadAspectChainCallback {
    void run(FileInfo fileInfo, FileStorage fileStorage, Consumer<InputStream> consumer);
}
