package com.async.digitkingdom.entity.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AddDeviceDto {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String deviceManufacturer;
    private String deviceStatus;
    private LocalDate createDate;
    private LocalDateTime updateDate;
    private String location;
    private Integer node_id;
    private Integer endpoint_id;
    private Integer cluster_id;
}
