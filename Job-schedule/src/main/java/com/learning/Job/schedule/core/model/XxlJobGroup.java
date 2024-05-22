package com.learning.Job.schedule.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XxlJobGroup {

    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private String appName;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private int order;

    @Getter
    @Setter
    private int addressType;

    @Getter
    @Setter
    private String addressList;

    private List<String> registryList;

    public List<String> getRegistryList() {
        if (this.addressList != null && this.addressList.trim().length() > 0) {
            this.registryList = new ArrayList(Arrays.asList(this.addressList.split(",")));
        }

        return this.registryList;
    }
}