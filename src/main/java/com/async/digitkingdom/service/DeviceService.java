package com.async.digitkingdom.service;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.Device;
import com.async.digitkingdom.entity.dto.AdjustLightDto;
import com.async.digitkingdom.entity.dto.UpdateDeviceDto;

public interface DeviceService {
//    Result addDevice(Device device);
    Result addDevice(Object object);

    Result selectByPage(int pageNum, int pageSize);

    Result selectById(String deviceId);

    Result deleteById(String deviceId);

    Result update(UpdateDeviceDto updateDeviceDto);

    Result turnOnLight();

    Result adjustLight(AdjustLightDto adjustLightDto);
}
