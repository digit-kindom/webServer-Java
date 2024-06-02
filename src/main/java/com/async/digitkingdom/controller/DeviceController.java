package com.async.digitkingdom.controller;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.Device;
import com.async.digitkingdom.entity.dto.AddDeviceDto;
import com.async.digitkingdom.entity.dto.UpdateDeviceDto;
import com.async.digitkingdom.service.DeviceService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Resource
    private DeviceService deviceService;

    @PostMapping("/add")
    public Result addDevice(@RequestBody AddDeviceDto addDeviceDto) {
        Device device = new Device();
        BeanUtils.copyProperties(addDeviceDto, device);
        return deviceService.addDevice(device);
    }

    @GetMapping("/list/{pageNum}/{pageSize}")
    public Result selectByPage(@PathVariable int pageNum, @PathVariable int pageSize) {
        return deviceService.selectByPage(pageNum, pageSize);
    }

    @GetMapping("/{deviceId}")
    public Result selectById(@PathVariable String deviceId) {
        return deviceService.selectById(deviceId);
    }

    @DeleteMapping("/{deviceId}")
    public Result deleteById(@PathVariable String deviceId) {
        return deviceService.deleteById(deviceId);
    }

    @PutMapping()
    public Result update(@RequestBody UpdateDeviceDto updateDeviceDto) {
        return deviceService.update(updateDeviceDto);
    }
}
