package com.learning.file.storage.aspect.callBack;

import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;

/**
 * 文件是否存在切面调用链结束回调
 */
public interface ExistsAspectChainCallback {
    boolean run(FileInfo fileInfo, FileStorage fileStorage);
}
