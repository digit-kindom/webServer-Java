package com.async.digitkingdom.service.impl;

import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.entity.Device;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.dto.AdjustLightDto;
import com.async.digitkingdom.entity.dto.UpdateDeviceDto;
import com.async.digitkingdom.mapper.DeviceMapper;
import com.async.digitkingdom.service.DeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Resource
    private DeviceMapper deviceMapper;

    @Override
    public Result addDevice(Device device) {
        device.setCreateDate(LocalDate.now());
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        device.setUserId(user.getUser().getId());
        device.setDeviceId(UUID.randomUUID().toString());
        deviceMapper.insert(device);
        return Result.ok("添加设备成功！");
    }

    @Override
    public Result selectByPage(int pageNum, int pageSize) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getUser().getId();
        PageHelper pageHelper = new PageHelper();
        pageHelper.startPage(pageNum, pageSize);
        List<Device> devices = deviceMapper.getAllByUserId(userId);
        PageInfo<Device> pageInfo = new PageInfo<>(devices);
        return Result.ok(pageInfo);
    }

    @Override
    public Result selectById(String deviceId) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getUser().getId();
        Device device = deviceMapper.getByDeviceId(deviceId);
        if(device == null){
            return Result.error("输入了错误的设备id");
        }
        if(!Objects.equals(device.getUserId(), userId)){
            return Result.error("没有权限访问当前设备！");
        }
        return Result.ok(device);
    }

    @Override
    public Result deleteById(String deviceId) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getUser().getId();
        Device device = deviceMapper.getByDeviceId(deviceId);
        if(device == null){
            return Result.error("输入了错误的设备id");
        }
        if(!Objects.equals(device.getUserId(), userId)){
            return Result.error("没有权限访问当前设备！");
        }
        deviceMapper.deleteByDeviceId(deviceId);
        return Result.ok("删除成功！");
    }

    @Override
    public Result update(UpdateDeviceDto updateDeviceDto) {
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getUser().getId();
        Device device = deviceMapper.getByDeviceId(updateDeviceDto.getDeviceId());
        if(device == null){
            return Result.error("输入了错误的设备id");
        }
        if(!Objects.equals(device.getUserId(), userId)){
            return Result.error("没有权限访问当前设备！");
        }
        updateDeviceDto.setUpdateTime(LocalDateTime.now());
        deviceMapper.update(updateDeviceDto);
        return Result.ok("修改设备信息成功!");
    }

    @Override
    public Result turnOnLight() {

        return null;
    }

    @Override
    public Result adjustLight(AdjustLightDto adjustLightDto) {

        return null;
    }
}
