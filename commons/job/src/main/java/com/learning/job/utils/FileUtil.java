package com.learning.job.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public FileUtil() {
    }

    public static boolean deleteRecursively(File root) {
        if (root != null && root.exists()) {
            if (root.isDirectory()) {
                File[] children = root.listFiles();
                if (children != null) {
                    File[] var2 = children;
                    int var3 = children.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        File child = var2[var4];
                        deleteRecursively(child);
                    }
                }
            }

            return root.delete();
        } else {
            return false;
        }
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

    }

    public static void writeFileContent(File file, byte[] data) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (Exception var12) {
            Exception e = var12;
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException var11) {
                    IOException e = var11;
                    logger.error(e.getMessage(), e);
                }
            }

        }

    }

    public static byte[] readFileContent(File file) {
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (Exception var13) {
            Exception e = var13;
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var12) {
                    IOException e = var12;
                    logger.error(e.getMessage(), e);
                }
            }

        }

        return filecontent;
    }
}
