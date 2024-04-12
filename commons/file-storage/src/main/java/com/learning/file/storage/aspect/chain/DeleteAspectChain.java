package com.learning.file.storage.aspect.chain;

import com.learning.file.storage.aspect.FileStorageAspect;
import com.learning.file.storage.aspect.callBack.DeleteAspectChainCallback;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.recorder.FileRecorder;
import com.learning.file.storage.storage.FileStorage;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

/**
 * 删除的切面调用链
 */
@Getter
@Setter
public class DeleteAspectChain {

    private DeleteAspectChainCallback callback;
    private Iterator<FileStorageAspect> aspectIterator;

    public DeleteAspectChain(Iterable<FileStorageAspect> aspects, DeleteAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public boolean next(FileInfo fileInfo, FileStorage fileStorage, FileRecorder fileRecorder) {
        if (aspectIterator.hasNext()) {//还有下一个
            return aspectIterator.next().deleteAround(this, fileInfo, fileStorage, fileRecorder);
        } else {
            return callback.run(fileInfo, fileStorage, fileRecorder);
        }
    }
}
