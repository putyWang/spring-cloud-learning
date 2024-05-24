package com.learning.job.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class XxlJobGroup {
    @Setter
    @Getter
    private int id;

    @Setter
    @Getter
    private String appName;

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private int order;

    @Setter
    @Getter
    private int addressType;

    @Setter
    @Getter
    private String addressList;

    private List<String> registryList;

    public List<String> getRegistryList() {
        if (this.addressList != null && this.addressList.trim().length() > 0) {
            this.registryList = new ArrayList(Arrays.asList(this.addressList.split(",")));
        }

        return this.registryList;
    }
}
