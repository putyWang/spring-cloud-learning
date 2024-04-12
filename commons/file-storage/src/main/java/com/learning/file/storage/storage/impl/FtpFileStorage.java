package com.learning.file.storage.storage.impl;

import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpMode;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class FtpFileStorage extends FileStorage {

    private Charset charset;
    private long connectionTimeout;
    private long soTimeout;
    private String serverLanguageCode;
    private String systemKey;
    private Boolean isActive;
    /**
     * 服务器主机地址
     */
    private String host;
    /**
     * ftp 服务端口
     */
    private int port;
    /**
     * 服务器用户名
     */
    private String user;
    /**
     * 服务器密码
     */
    private String password;

    private Ftp getClient() {
        FtpConfig config = FtpConfig.create().
                setHost(host).setPort(port).setUser(user).setPassword(password).
                setCharset(this.charset).
                setConnectionTimeout(this.connectionTimeout).
                setSoTimeout(this.soTimeout).
                setServerLanguageCode(this.serverLanguageCode).
                setSystemKey(this.systemKey);
        return new Ftp(config, this.isActive ? FtpMode.Active : FtpMode.Passive);
    }

    @Override
    public boolean uploadFile(String newFileKey, InputStream inputStream) {
        String generateFileName = newFileKey.substring(newFileKey.lastIndexOf("/") + 1);
        String uploadPath = "/" + newFileKey.replace(generateFileName, "");
        return getClient().upload(uploadPath, generateFileName, inputStream);
    }

    @Override
    public boolean deleteFile(String fileKey) throws Exception {
        getClient().delFile(fileKey);
    }

    @Override
    public boolean fileExists(String fileKey) throws Exception {
        return getClient().existFile(fileKey);
    }

    @Override
    public InputStream downloadFile(String fileKey) throws Exception {
        Ftp client = getClient();
        int slashIndex = fileKey.lastIndexOf("/");
        String dir = fileKey.substring(0, slashIndex);
        String fileName = fileKey.substring(slashIndex).replace("/", "");
        client.cd(dir);
        return client.getClient().retrieveFileStream(fileName);
    }

    @Override
    public void updateUrl(FileInfo fileInfo) {

    }
}
