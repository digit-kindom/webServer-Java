package com.async.digitkingdom.common;

import java.util.HashMap;
import java.util.Map;

public class ClusterConst {
    public static final Map<Integer, String> clusterConstMap = new HashMap<Integer, String>();
    static {
        clusterConstMap.put(6, "On_Off");
        clusterConstMap.put(8, "Level_Control");
        clusterConstMap.put(9, "Alarms");
        clusterConstMap.put(1026, "Temperature Measurement");
        clusterConstMap.put(1037, "Carbon Dioxide Concentration Measurement");
    }
}
