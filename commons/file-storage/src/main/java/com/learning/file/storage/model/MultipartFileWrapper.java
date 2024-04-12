/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.learning.file.storage.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * MultipartFile 的包装类
 */
@Setter
public class MultipartFileWrapper implements MultipartFile {
    /**
     * 新建存储文件名
     */
    private String name;
    /**
     * 原始文件名
     */
    private String originalFilename;
    /**
     * 网络文件类型
     */
    private String contentType;
    /**
     * 网络文件对象
     */
    @Getter
    private MultipartFile multipartFile;

    public MultipartFileWrapper(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
        this.contentType = multipartFile.getContentType();
        this.name = multipartFile.getName();
        this.originalFilename = multipartFile.getOriginalFilename();
    }

    @Override
    public String getName() {
        return name != null ? name : multipartFile.getName();
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename != null ? originalFilename : multipartFile.getOriginalFilename();
    }

    @Override
    @Nullable
    public String getContentType() {
        return contentType != null ? contentType : multipartFile.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return multipartFile.isEmpty();
    }

    @Override
    public long getSize() {
        return multipartFile.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return multipartFile.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }


    @Override
    public Resource getResource() {
        return multipartFile.getResource();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        multipartFile.transferTo(dest);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        multipartFile.transferTo(dest);
    }
}
