package com.async.digitkingdom.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.entity.Args;
import com.async.digitkingdom.entity.Device;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.Payload;
import com.async.digitkingdom.entity.dto.AddDeviceDto;
import com.async.digitkingdom.entity.dto.TurnOnLightDto;
import com.async.digitkingdom.entity.dto.UpdateDeviceDto;
import com.async.digitkingdom.mapper.DeviceMapper;
import com.async.digitkingdom.mapper.OperationHistoryMapper;
import com.async.digitkingdom.service.DeviceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Resource
    private DeviceService deviceService;
    @Resource
    private MyWebSocketClient webSocketClient;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private OperationHistoryMapper operationHistoryMapper;

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

    @PostMapping("/lightOperation")
    public Result lightOperation(@RequestParam String deviceId){
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        TurnOnLightDto turnOnLightDto = new TurnOnLightDto();
        Device byDeviceId = deviceMapper.getByDeviceId(deviceId);
        if(byDeviceId == null){
            return Result.error("传入了错误的id！");
        }
        UpdateDeviceDto updateDeviceDto = new UpdateDeviceDto();
        turnOnLightDto.setCommand("device_command");
        Args args = new Args();
        args.setNode_id(byDeviceId.getNode_id());
        args.setEndpoint_id(byDeviceId.getEndpoint_id());
        args.setCluster_id(byDeviceId.getCluster_id());
//        Payload payload = new Payload();
//        payload.setLevel(100);
//        payload.setOptionsMask(0);
//        payload.setTransitionTime(0);
//        payload.setOptionsOverride(0);
//        args.setPayload(payload);
        if(byDeviceId.getDeviceStatus().equals("On")){
            turnOnLightDto.setMessage_id(UUID.randomUUID().toString());
            args.setCommand_name("Off");
            turnOnLightDto.setArgs(args);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(turnOnLightDto);
            String json = jsonObject.toString();
            webSocketClient.send(json);
            operationHistoryMapper.insert("Off",userId,deviceId, LocalDateTime.now(), UUID.randomUUID().toString());
            updateDeviceDto.setDeviceStatus("Off");
        }else {
            turnOnLightDto.setMessage_id(UUID.randomUUID().toString());
            args.setCommand_name("On");
            turnOnLightDto.setArgs(args);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(turnOnLightDto);
            String json = jsonObject.toString();
            webSocketClient.send(json);
            operationHistoryMapper.insert("On",userId,deviceId, LocalDateTime.now(), UUID.randomUUID().toString());
            updateDeviceDto.setDeviceStatus("On");
        }
        deviceMapper.update(updateDeviceDto);
        return Result.ok("完成操作！");
    }
}
