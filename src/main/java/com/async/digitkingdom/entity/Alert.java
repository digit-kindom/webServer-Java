package com.async.digitkingdom.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Alert {
    private String deviceId;
    private LocalDateTime createTime;
    private String event;
    private String level;
    private Boolean isConfirmed;
    private Boolean isDeleted;
    private Integer alertId;
}
