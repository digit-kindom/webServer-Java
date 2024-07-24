package com.async.digitkingdom.common;

import java.util.HashMap;
import java.util.Map;

public class TemperatureSensorConst {
    public static final Map<Integer, String> attributes = new HashMap<Integer, String>();
    static {
        attributes.put(0, "MeasuredValue");
        attributes.put(1, "MinMeasuredValue");
        attributes.put(2, "MaxMeasuredValue");
        attributes.put(3, "Tolerance");
    }
}
