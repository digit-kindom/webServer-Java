package com.async.digitkingdom.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Device {
    private String deviceId;
    private Integer userId;
    private String deviceName;
    private String deviceType;
    private String deviceModel;
    private String deviceManufacturer;
    private String deviceStatus;
    private LocalDate createDate;
    private LocalDateTime updateDate;
    private String location;
    private Integer nodeId;
    private Integer endpointId;
    private Integer clusterId;
    private Integer node_id;
    private Integer endpoint_id;
    private Integer cluster_id;
}
