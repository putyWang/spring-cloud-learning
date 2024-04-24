package com.learning.file.storage.storage.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.*;
import com.learning.file.storage.exception.FileStorageException;
import com.learning.file.storage.model.FileInfo;
import com.learning.file.storage.storage.FileStorage;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;

@Log4j2
public class SftpFileStorage extends FileStorage {

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

    private static final UserInfo defaultUserInfo = new UserInfo() {
        @Override
        public String getPassphrase() {
            System.out.println("getPassphrase");
            return null;
        }
        @Override
        public String getPassword() {
            System.out.println("getPassword");
            return null;
        }
        @Override
        public boolean promptPassword(String s) {
            log.info("promptPassword:{}", s);
            return false;
        }
        @Override
        public boolean promptPassphrase(String s) {
            log.info("promptPassphrase:{}", s);
            return false;
        }
        @Override
        public boolean promptYesNo(String s) {
            log.info("promptYesNo:{}", s);
            return true;//notice here!
        }
        @Override
        public void showMessage(String s) {
            log.info("showMessage:{}", s);
        }
    };

    public Session getSession() {
        Session session = null;

        try {
            session = new JSch().getSession(user, host, port);
            session.setPassword(password);
            session.setUserInfo(defaultUserInfo);
            return session;
        } catch (Exception e) {
            JschUtil.close(session);
            throw new FileStorageException("sftp session 创建失败", e);
        }
    }

    @Override
    public boolean uploadFile(String newFileKey, InputStream inputStream) throws Exception {
        // 1.获取文件名与文件夹名
        String generateFileName = newFileKey.substring(newFileKey.lastIndexOf("/") + 1);
        String uploadPath = "/" + newFileKey.replace(generateFileName, "");
        // 2.连接服务器
        ChannelSftp channel = connectChannel();
        log.info("上传文件到远程SFTP服务器,远程服务器当前位置:{}" , channel.pwd());
        log.info("上传文件到远程SFTP服务器,进入远程服务器文件:{}" , uploadPath);
        // 3.进入要上传文件的文件夹
        this.cdDir(channel, uploadPath);
        String now = channel.pwd();
        log.info("上传文件到远程SFTP服务器,远程服务器当前位置:{}" , now);
        // 4.上传文件
        channel.put(inputStream, generateFileName, 0);
        // 5.关闭连接
        channelClose(channel);
        return true;
    }

    @Override
    public boolean deleteFile(String fileKey) throws Exception {
        // 1.连接服务器
        ChannelSftp channel = connectChannel();
        // 2.删除文件
        channel.rm(fileKey);
        // 3.关闭连接
        channelClose(channel);
        return true;
    }

    @Override
    public boolean fileExists(String fileKey) throws Exception {
        return false;
    }

    @Override
    public InputStream downloadFile(String fileKey) throws Exception {
        // 1.连接服务器
        ChannelSftp channel = connectChannel();
        // 2.删除文件
        return channel.get(fileKey);
    }

    @Override
    public void updateUrl(FileInfo fileInfo) {
        fileInfo.setUrl(this.getDomain() + fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename());
        fileInfo.setThUrl(this.getDomain() + fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getThFilename());
    }

    /**
     * 连接管道
     * @return
     * @throws Exception
     */
    private ChannelSftp connectChannel() throws Exception{
        Session session = getSession();
        session.connect();
        ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
        channel.connect();
        return channel;
    }

    /**
     * 关闭管道
     * @param channel sftp 管道
     * @return
     * @throws Exception
     */
    private void channelClose(Channel channel) throws Exception{
        Session session = channel.getSession();
        channel.disconnect();
        session.disconnect();
    }

    /**
     * 进入服务器指定文件夹
     * @param sftp sftp 管道
     * @param dir 要进入的文件夹
     * @throws SftpException
     */
    private void cdDir(ChannelSftp sftp, String dir) throws SftpException {
        // 1.进入根文件夹
        sftp.cd("/");
        // 2.进入目标文件夹
        for(String folder :  dir.split("/")) {
            if (folder.length() > 0) {
                try {
                    sftp.cd(folder);
                } catch (Exception var9) {
                    // 3.文件夹不存在时，创建指定文件夹
                    sftp.mkdir(folder);
                    sftp.cd(folder);
                }
            }
        }
    }
}
