package com.learning.file.storage.aspect.chain;

import com.learning.file.storage.aspect.FileStorageAspect;
import com.learning.file.storage.aspect.callBack.UploadAspectChainCallback;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.model.UploadPretreatment;
import com.learning.file.storage.recorder.FileRecorder;
import com.learning.file.storage.storage.FileStorage;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

/**
 * 上传的切面调用链
 */
@Getter
@Setter
public class UploadAspectChain {

    private UploadAspectChainCallback callback;
    private Iterator<FileStorageAspect> aspectIterator;

    public UploadAspectChain(Iterable<FileStorageAspect> aspects, UploadAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public FileInfo next(FileInfo fileInfo, UploadPretreatment pre, FileStorage fileStorage, FileRecorder fileRecorder) {
        if (aspectIterator.hasNext()) {//还有下一个
            return aspectIterator.next().uploadAround(this, fileInfo, pre, fileStorage, fileRecorder);
        } else {
            return callback.run(fileInfo, pre, fileStorage, fileRecorder);
        }
    }
}
