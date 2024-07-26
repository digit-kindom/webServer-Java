package com.async.digitkingdom.common;

import java.util.HashMap;
import java.util.Map;

public class ConcentrationMeasurementConst {
    public static final Map<Integer, String> attributes = new HashMap<Integer, String>();
    static {
        attributes.put(0,"MeasuredValue");
        attributes.put(1,"MinMeasuredValue");
        attributes.put(2,"MaxMeasuredValue");
        attributes.put(3,"PeakMeasuredValue");
        attributes.put(4,"PeakMeasuredValueWindow");
        attributes.put(5,"AverageMeasuredValue");
        attributes.put(6,"AverageMeasuredValueWindow");
        attributes.put(7,"Uncertainty");
        attributes.put(8,"MeasurementUnit");
        attributes.put(9,"MeasurementMedium");
        attributes.put(10,"LevelValue");
    }
}
