package com.async.digitkingdom.entity.vo;

import lombok.Data;

@Data
public class TemperatureSensorVo {
    private Integer MeasuredValue;
    private Integer MinMeasuredValue;
    private Integer MaxMeasuredValue;
    private Integer Tolerance;
}
