package com.learning.job.glue;

import lombok.Getter;

public enum GlueTypeEnum {
    BEAN("BEAN", false, (String)null, (String)null),
    GLUE_GROOVY("GLUE(Java)", false, (String)null, (String)null),
    GLUE_SHELL("GLUE(Shell)", true, "bash", ".sh"),
    GLUE_PYTHON("GLUE(Python)", true, "python", ".py"),
    GLUE_PHP("GLUE(PHP)", true, "php", ".php"),
    GLUE_NODEJS("GLUE(Nodejs)", true, "node", ".js"),
    GLUE_POWERSHELL("GLUE(PowerShell)", true, "powershell ", ".ps1");

    @Getter
    private String desc;

    @Getter
    private boolean isScript;

    @Getter
    private String cmd;

    @Getter
    private String suffix;

    GlueTypeEnum(String desc, boolean isScript, String cmd, String suffix) {
        this.desc = desc;
        this.isScript = isScript;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public static GlueTypeEnum match(String name) {
        for(GlueTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
