package com.learning.file.storage.aspect.chain;

import com.learning.file.storage.aspect.FileStorageAspect;
import com.learning.file.storage.aspect.callBack.DownloadThAspectChainCallback;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 下载缩略图的切面调用链
 */
@Getter
@Setter
public class DownloadThAspectChain {

    private DownloadThAspectChainCallback callback;
    private Iterator<FileStorageAspect> aspectIterator;

    public DownloadThAspectChain(Iterable<FileStorageAspect> aspects, DownloadThAspectChainCallback callback) {
        this.aspectIterator = aspects.iterator();
        this.callback = callback;
    }

    /**
     * 调用下一个切面
     */
    public void next(FileInfo fileInfo, FileStorage fileStorage, Consumer<InputStream> consumer) {
        if (aspectIterator.hasNext()) {//还有下一个
            aspectIterator.next().downloadThAround(this, fileInfo, fileStorage, consumer);
        } else {
            callback.run(fileInfo, fileStorage, consumer);
        }
    }
}
