package com.async.digitkingdom.common;

import java.util.HashMap;
import java.util.Map;

public class DeviceTypeConst {
    public static final Map<Integer, String> deviceConstMap = new HashMap<Integer, String>();
    static {
        deviceConstMap.put(0x0104,"Dimmer Switch");
        deviceConstMap.put(0x0105,"Color Dimmer Switch");
        deviceConstMap.put(0x000F,"Generic Switch");
        deviceConstMap.put(0x0106,"Light Sensor");
    }
}
