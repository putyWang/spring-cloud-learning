package com.learning.job.handler.impl;

import com.learning.job.biz.model.ReturnT;
import com.learning.job.glue.GlueTypeEnum;
import com.learning.job.handler.IJobHandler;
import com.learning.job.log.XxlJobFileAppender;
import com.learning.job.log.XxlJobLogger;
import com.learning.job.utils.ScriptUtil;
import com.learning.job.utils.ShardingUtil;
import lombok.Getter;

import java.io.File;

public class ScriptJobHandler extends IJobHandler {
    private int jobId;
    @Getter
    private long glueUpdateTime;
    private String glueSource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(int jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType) {
        this.jobId = jobId;
        this.glueUpdateTime = glueUpdatetime;
        this.glueSource = gluesource;
        this.glueType = glueType;
        File glueSrcPath = new File(XxlJobFileAppender.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFileList = glueSrcPath.listFiles();
            if (glueSrcFileList != null && glueSrcFileList.length > 0) {
                File[] var8 = glueSrcFileList;
                int var9 = glueSrcFileList.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    File glueSrcFileItem = var8[var10];
                    if (glueSrcFileItem.getName().startsWith(jobId + "_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    public ReturnT<String> execute(String param) throws Exception {
        if (!this.glueType.isScript()) {
            return new ReturnT(IJobHandler.FAIL.getCode(), "glueType[" + this.glueType + "] invalid.");
        } else {
            String cmd = this.glueType.getCmd();
            String scriptFileName = XxlJobFileAppender.getGlueSrcPath().concat(File.separator).concat(String.valueOf(this.jobId))
                    .concat("_").concat(String.valueOf(this.glueUpdateTime)).concat(this.glueType.getSuffix());
            File scriptFile = new File(scriptFileName);
            if (!scriptFile.exists()) {
                ScriptUtil.markScriptFile(scriptFileName, this.glueSource);
            }

            String logFileName = XxlJobFileAppender.contextHolder.get();
            ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
            String[] scriptParams = new String[]{param, String.valueOf(shardingVO.getIndex()), String.valueOf(shardingVO.getTotal())};
            XxlJobLogger.log("----------- script file:" + scriptFileName + " -----------");
            int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);
            return exitValue == 0 ? IJobHandler.SUCCESS : new ReturnT(IJobHandler.FAIL.getCode(), "script exit value(" + exitValue + ") is failed");
        }
    }
}
