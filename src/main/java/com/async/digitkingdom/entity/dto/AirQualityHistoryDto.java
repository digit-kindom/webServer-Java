package com.async.digitkingdom.entity.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AirQualityHistoryDto {
    private Integer id;
    private Integer nodeId;
    private String substanceType;
    private Float value;
    private LocalDateTime detectTime;
    private Float unit;
}
