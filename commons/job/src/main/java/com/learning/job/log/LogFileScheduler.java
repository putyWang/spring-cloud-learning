package com.learning.job.log;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

@Component
@EnableScheduling
@Log4j2
public class LogFileScheduler {
    @Value("${yanhua.cloud.job.log.keepDays:0}")
    private int keepDays;
    @Value("${yanhua.cloud.job.log.keepCount:10000}")
    private int keepCount;

    public LogFileScheduler() {
    }

    @Scheduled(
            cron = "0 0 0 * * ?"
    )
    public void deleteLogFiles() {
        if (this.keepDays > 0) {
            log.info("开始删除日志文件，配置为--->admin.job.log.keepDays ：" + this.keepDays + ",admin.job.log.keepCount :" + this.keepCount + ",fileBasePath:" + XxlJobFileAppender.getLogPath());
            Calendar cal = Calendar.getInstance();
            cal.add(5, -this.keepDays);
            Date date = cal.getTime();
            File directory = new File(XxlJobFileAppender.getLogPath());
            if (directory.isDirectory() && directory.exists()) {
                File[] fileList = directory.listFiles();

                for(int i = 0; i < fileList.length; ++i) {
                    try {
                        File file = fileList[i];
                        String fileName = file.getName();
                        if (!XxlJobFileAppender.gluesource.equals(fileName) && !XxlJobFileAppender.callbacklog.equals(fileName)) {
                            Date createDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(fileName);
                            if (createDate.compareTo(date) < 0) {
                                this.deleteDateFiles(file.getAbsolutePath());
                            }
                        } else if (this.keepCount >= 0) {
                            String path = XxlJobFileAppender.getLogPath() + File.separator + fileName;
                            File folder = new File(path);
                            if (folder.isDirectory() && folder.exists()) {
                                Path path1 = Paths.get(path);
                                Files.list(path1).filter((x$0) -> Files.isRegularFile(x$0))
                                        .sorted(Comparator.comparingLong((f) -> -f.toFile().lastModified()))
                                        .skip(this.keepCount).forEach((f) -> {
                                            f.toFile().delete();
                                        });
                            }
                        }
                    } catch (Exception var11) {
                        log.info("删除失败，filename：" + fileList[i].getName());
                    }
                }
            }
        }

    }

    private boolean deleteDateFiles(String dirPath) {
        boolean flag = true;
        File directory = new File(dirPath);
        if (directory.isDirectory() && directory.exists()) {
            File[] fileList = directory.listFiles();

            for(int i = 0; i < fileList.length; ++i) {
                File file = fileList[i];
                if (file.isFile()) {
                    flag = deleteLogFile(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }

                if (file.isDirectory()) {
                    flag = this.deleteDateFiles(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }

            if (!flag) {
                log.info("删除文件夹失败！");
                return false;
            }

            directory.delete();
            log.info("删除{}完成！", dirPath);
        }

        return false;
    }

    private static boolean deleteLogFile(String fileName) {
        File file = new File(fileName);
        return file.exists() && file.isFile() && file.delete();
    }
}
