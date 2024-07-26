package com.async.digitkingdom.entity.vo;

import lombok.Data;

@Data
public class ConcentrationMeasurementVo {
    private String target;
    private Float MeasuredValue;
    private Float MinMeasuredValue;
    private Float MaxMeasuredValue;
    private Float PeakMeasuredValue;
    private Float PeakMeasuredValueWindow;
    private Float AverageMeasuredValue;
    private Float AverageMeasuredValueWindow;
    private Float Uncertainty;
    private Float MeasurementUnit;
    private Float MeasurementMedium;
    private Float LevelValue;
}
