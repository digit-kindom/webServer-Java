package com.async.digitkingdom.common;

import java.util.HashMap;
import java.util.Map;

public class DeviceTypeConst {
    public static final Map<Integer, String> deviceConstMap = new HashMap<Integer, String>();
    static {
        deviceConstMap.put(0x0104,"Dimmer Switch");
        deviceConstMap.put(0x010B,"On/Off Plugin");
        deviceConstMap.put(0x0105,"Color Dimmer Switch");
        deviceConstMap.put(0x000F,"Generic Switch");
        deviceConstMap.put(0x0106,"Light Sensor");
        deviceConstMap.put(0x0000,"自定义设备");
        deviceConstMap.put(0x002C,"Air Quality Sensor");
        deviceConstMap.put(0x0202,"Window Covering");
        deviceConstMap.put(0x22B8,"Camera");
        deviceConstMap.put(0x22B9,"Colorful Light");
    }
}
