package com.async.digitkingdom.common;

import java.util.HashMap;
import java.util.Map;

public class AirConditionConst {
    public static final Map<Integer, String> airConditionCluster = new HashMap<Integer, String>();
    static {
        airConditionCluster.put(1026, "Temperature Measurement");
        airConditionCluster.put(1037, "Carbon Dioxide Concentration Measurement");
        airConditionCluster.put(1036, "Carbon Monoxide Concentration Measurement");
        airConditionCluster.put(1043, "Nitrogen Dioxide Concentration Measurement");
        airConditionCluster.put(1045, "Ozone Concentration Measurement");
        airConditionCluster.put(1066, "PM2.5 Concentration Measurement");
        airConditionCluster.put(1067, "Formaldehyde Concentration Measurement");
        airConditionCluster.put(1068, "PM1 Concentration Measurement");
        airConditionCluster.put(1069, "PM10 Concentration Measurement");
        airConditionCluster.put(1070, "Total Volatile Organic Compounds (TVOC)");
        airConditionCluster.put(1071, "Radon Concentration Measurement");
    }
}