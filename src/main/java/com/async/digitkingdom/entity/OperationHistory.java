package com.async.digitkingdom.entity;

import lombok.Data;

@Data
public class OperationHistory {
    private String id;
    private String deviceId;
    private String operationName;
    private String operationTime;
    private String operator;
}
