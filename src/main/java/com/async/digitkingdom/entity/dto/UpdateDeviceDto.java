package com.async.digitkingdom.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UpdateDeviceDto {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String deviceManufacturer;
    private String deviceStatus;
    private String location;
    private LocalDateTime updateTime;
}
