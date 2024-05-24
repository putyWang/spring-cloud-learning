package com.learning.job.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.learning.job.biz.model.LogResult;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class XxlJobFileAppender {
    
    public static final InheritableThreadLocal<String> contextHolder = new InheritableThreadLocal();
    public static String logBasePath = "/data/applogs/yh-job/jobhandler";
    public static String gluesource = "gluesource";
    public static String callbacklog = "callbacklog";
    private static String glueSrcPath;

    public XxlJobFileAppender() {
    }

    public static void initLogPath(String logPath) {
        if (logPath != null && logPath.trim().length() > 0) {
            logBasePath = logPath;
        }

        File logPathDir = new File(logBasePath);
        if (!logPathDir.exists()) {
            logPathDir.mkdirs();
        }

        logBasePath = logPathDir.getPath();
        File glueBaseDir = new File(logPathDir, gluesource);
        if (!glueBaseDir.exists()) {
            glueBaseDir.mkdirs();
        }

        glueSrcPath = glueBaseDir.getPath();
    }

    public static String getLogPath() {
        return logBasePath;
    }

    public static String getGlueSrcPath() {
        return glueSrcPath;
    }

    public static String makeLogFileName(Date triggerDate, long logId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File logFilePath = new File(getLogPath(), sdf.format(triggerDate));
        if (!logFilePath.exists()) {
            logFilePath.mkdir();
        }

        String logFileName = logFilePath.getPath().concat(File.separator).concat(String.valueOf(logId)).concat(".log");
        return logFileName;
    }

    public static void appendLog(String logFileName, String appendLog) {
        if (logFileName != null && logFileName.trim().length() != 0) {
            File logFile = new File(logFileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException var15) {
                    IOException e = var15;
                    log.error(e.getMessage(), e);
                    return;
                }
            }

            if (appendLog == null) {
                appendLog = "";
            }

            appendLog = appendLog + "\r\n";
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(logFile, true);
                fos.write(appendLog.getBytes("utf-8"));
                fos.flush();
            } catch (Exception var14) {
                Exception e = var14;
                log.error(e.getMessage(), e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException var13) {
                        IOException e = var13;
                        log.error(e.getMessage(), e);
                    }
                }

            }

        }
    }

    public static LogResult readLog(String logFileName, int fromLineNum) {
        if (logFileName != null && logFileName.trim().length() != 0) {
            File logFile = new File(logFileName);
            if (!logFile.exists()) {
                return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
            } else {
                StringBuffer logContentBuffer = new StringBuffer();
                int toLineNum = 0;
                LineNumberReader reader = null;

                try {
                    IOException e;
                    try {
                        reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
                        e = null;

                        String line;
                        while((line = reader.readLine()) != null) {
                            toLineNum = reader.getLineNumber();
                            if (toLineNum >= fromLineNum) {
                                logContentBuffer.append(line).append("\n");
                            }
                        }
                    } catch (IOException var15) {
                        e = var15;
                        log.error(e.getMessage(), e);
                    }
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException var14) {
                            IOException e = var14;
                            log.error(e.getMessage(), e);
                        }
                    }

                }

                LogResult logResult = new LogResult(fromLineNum, toLineNum, logContentBuffer.toString(), false);
                return logResult;
            }
        } else {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
        }
    }

    public static String readLines(File logFile) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
            if (reader == null) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                String var4 = sb.toString();
                return var4;
            }
        } catch (IOException var15) {
            IOException e = var15;
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException var14) {
                    IOException e = var14;
                    log.error(e.getMessage(), e);
                }
            }

        }
    }

    static {
        glueSrcPath = logBasePath.concat(File.separator + gluesource);
    }
}

