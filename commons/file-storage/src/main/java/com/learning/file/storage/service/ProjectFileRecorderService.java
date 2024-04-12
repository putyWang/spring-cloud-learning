package com.learning.file.storage.service;

import com.learning.file.storage.model.FileInfo;

;

/**
 * 文件记录记录者接口
 */
public interface ProjectFileRecorderService {

    /**
     * 保存文件记录
     */
    boolean record(FileInfo fileInfo);

    /**
     * 根据 url 获取文件记录
     */
    FileInfo getByUrl(String url);

    /**
     * 根据 url 删除文件记录
     */
    boolean delete(String url);
}
