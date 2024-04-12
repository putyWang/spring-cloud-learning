package com.learning.file.storage.aspect;

import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.service.ProjectFileRecorderService;
import com.learning.file.storage.service.ProjectFileStorageService;
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
    public boolean next(FileInfo fileInfo, ProjectFileStorageService fileStorage, ProjectFileRecorderService fileRecorder) {
        if (aspectIterator.hasNext()) {//还有下一个
            return aspectIterator.next().deleteAround(this, fileInfo, fileStorage, fileRecorder);
        } else {
            return callback.run(fileInfo, fileStorage, fileRecorder);
        }
    }
}
