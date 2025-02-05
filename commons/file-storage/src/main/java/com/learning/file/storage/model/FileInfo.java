package com.learning.file.storage.model;


import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.learning.core.utils.CommonBeanUtil;
import com.learning.core.utils.StringUtil;
import com.learning.file.storage.exception.FileStorageException;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;

@Data
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 文件id
     */
    private Integer id;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 文件大小，单位字节
     */
    private Long size;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 基础存储路径
     */
    private String basePath;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * 存储平台
     */
    private String platform;

    /**
     * 缩略图访问路径
     */
    private String thUrl;

    /**
     * 缩略图名称
     */
    private String thFilename;

    /**
     * 缩略图大小，单位字节
     */
    private Long thSize;

    /**
     * 文件所属对象id
     */
    private String objectId;

    /**
     * 文件所属对象类型，例如用户头像，评价图片
     */
    private String objectType;

    /**
     * 上传文件用户ID
     */
    private String uploadUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 根据 pre 构造文件信息
     * @param pre 文件预处理对象
     * @return
     */
    public static FileInfo buildByUploadPretreatment (UploadPretreatment pre){
        // 1.获取 pre 相同字段信息
        FileInfo fileInfo = CommonBeanUtil.copyAndFormat(FileInfo.class, pre);
        fileInfo.setCreateTime(new Date());
        // 2.根据 pre 中文件信息设置文件相关信息
        MultipartFile file = pre.getFileWrapper();
        if (file == null) throw new FileStorageException("文件不允许为 null ！");
        if (pre.getPlatform() == null) throw new FileStorageException("platform 不允许为 null ！");
        fileInfo.setSize(file.getSize());
        fileInfo.setOriginalFilename(file.getOriginalFilename());
        fileInfo.setExt(FileNameUtil.getSuffix(file.getOriginalFilename()));
        fileInfo.setFilename(StringUtil.isNotBlank(pre.getSaveFilename()) ? pre.getSaveFilename() :
                IdUtil.objectId() + (StrUtil.isEmpty(fileInfo.getExt()) ? StrUtil.EMPTY : "." + fileInfo.getExt()));

        // 3.设置缩略图信息
        byte[] thumbnailBytes = pre.getThumbnailBytes();
        if (thumbnailBytes != null) {
            fileInfo.setThSize((long) thumbnailBytes.length);
            if (StringUtil.isNotBlank(pre.getSaveThFilename())) {
                fileInfo.setThFilename(pre.getSaveThFilename() + pre.getThumbnailSuffix());
            } else {
                fileInfo.setThFilename(fileInfo.getFilename() + pre.getThumbnailSuffix());
            }
        }

        return fileInfo;
    }
}
