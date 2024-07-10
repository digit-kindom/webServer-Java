package com.async.digitkingdom.entity;

import lombok.Data;

@Data
public class DeviceCluster {
    private String deviceId;
    private Integer cluster;
    private Integer endpoint;
}
