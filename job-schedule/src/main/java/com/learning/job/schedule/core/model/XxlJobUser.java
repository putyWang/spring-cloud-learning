package com.learning.job.schedule.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
public class XxlJobUser {
    private int id;
    private String username;
    private String password;
    private int role;
    private String permission;

    public boolean validPermission(int jobGroup) {
        if (this.role == 1) {
            return true;
        } else {
            if (StringUtils.hasText(this.permission)) {
                for(String permissionItem : permission.split(",")) {
                    if (String.valueOf(jobGroup).equals(permissionItem)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
