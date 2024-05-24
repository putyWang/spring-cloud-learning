package com.learning.job.biz.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistryParam implements Serializable {
    private static final long serialVersionUID = 42L;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
}
