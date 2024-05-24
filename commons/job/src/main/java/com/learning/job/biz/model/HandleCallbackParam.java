package com.learning.job.biz.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;
    private long logId;
    private long logDateTim;
    private ReturnT<String> executeResult;
}
