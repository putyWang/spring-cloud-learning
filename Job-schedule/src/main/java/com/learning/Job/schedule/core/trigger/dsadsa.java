package com.learning.Job.schedule.core.trigger;

import com.xxl.job.admin.core.util.I18nUtil;

public enum TriggerTypeEnum {
    MANUAL(I18nUtil.getString("jobconf_trigger_type_manual")),
    CRON(I18nUtil.getString("jobconf_trigger_type_cron")),
    RETRY(I18nUtil.getString("jobconf_trigger_type_retry")),
    PARENT(I18nUtil.getString("jobconf_trigger_type_parent")),
    API(I18nUtil.getString("jobconf_trigger_type_api"));

    private String title;

    private TriggerTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
