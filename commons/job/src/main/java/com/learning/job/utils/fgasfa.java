package com.learning.job.utils;

import com.learning.job.log.XxlJobLogger;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class ScriptUtil {
    public ScriptUtil() {
    }

    public static void markScriptFile(String scriptFileName, String content) throws IOException {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(scriptFileName);
            fileOutputStream.write(content.getBytes("UTF-8"));
            fileOutputStream.close();
        } catch (Exception e) {
            throw e;
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

        }

    }

    public static int execToFile(String command, String scriptFile, String logFile, String... params) throws IOException {
        FileOutputStream fileOutputStream = null;

        byte var6;
        try {
            fileOutputStream = new FileOutputStream(logFile, true);
            PumpStreamHandler streamHandler = new PumpStreamHandler(fileOutputStream, fileOutputStream, (InputStream)null);
            CommandLine commandline = new CommandLine(command);
            commandline.addArgument(scriptFile);
            if (params != null && params.length > 0) {
                commandline.addArguments(params);
            }

            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues((int[])null);
            exec.setStreamHandler(streamHandler);
            int exitValue = exec.execute(commandline);
            int var9 = exitValue;
            return var9;
        } catch (Exception var19) {
            Exception e = var19;
            XxlJobLogger.log(e);
            var6 = -1;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException var18) {
                    IOException e = var18;
                    XxlJobLogger.log(e);
                }
            }

        }

        return var6;
    }
}
