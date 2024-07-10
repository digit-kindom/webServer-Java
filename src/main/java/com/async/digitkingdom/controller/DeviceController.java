package com.async.digitkingdom.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.async.digitkingdom.common.ClusterConst;
import com.async.digitkingdom.common.DeviceTypeConst;
import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.entity.Args;
import com.async.digitkingdom.entity.Device;
import com.async.digitkingdom.entity.LoginUser;
import com.async.digitkingdom.entity.Payload;
import com.async.digitkingdom.entity.dto.AddDeviceDto;
import com.async.digitkingdom.entity.dto.AdjustLightDto;
import com.async.digitkingdom.entity.dto.TurnOnLightDto;
import com.async.digitkingdom.entity.dto.UpdateDeviceDto;
import com.async.digitkingdom.mapper.DeviceClusterMapper;
import com.async.digitkingdom.mapper.DeviceMapper;
import com.async.digitkingdom.mapper.OperationHistoryMapper;
import com.async.digitkingdom.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

@RestController
@RequestMapping("/device")
public class DeviceController {
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);
    @Resource
    private DeviceService deviceService;
    @Resource
    private MyWebSocketClient webSocketClient;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private OperationHistoryMapper operationHistoryMapper;
    @Autowired
    private DeviceClusterMapper deviceClusterMapper;

    @PostMapping("/add")
    public Result addDevice(@RequestBody AddDeviceDto addDeviceDto) {
        Device device = new Device();
        BeanUtils.copyProperties(addDeviceDto, device);
        log.info("已添加设备{}", JSON.toJSONString(device));
        return deviceService.addDevice(device);
    }

//    @PostMapping("/add")
//    public Result addDevice(@RequestBody AddDeviceDto addDeviceDto) {
//        Device device = new Device();
//        BeanUtils.copyProperties(addDeviceDto, device);
//        log.info("已添加设备{}", JSON.toJSONString(device));
//        return deviceService.addDevice(device);
//    }

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

    @PostMapping("/lightOperation/{deviceId}")
    public Result lightOperation(@PathVariable String deviceId){
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
        args.setNode_id(byDeviceId.getNodeId());
        args.setEndpoint_id(byDeviceId.getEndpointId());
        args.setCluster_id(6);
        Payload payload = new Payload();
        payload.setLevel(100);
        payload.setOptionsMask(0);
        payload.setTransitionTime(0);
        payload.setOptionsOverride(0);
        args.setPayload(payload);
        String deviceStatus = byDeviceId.getDeviceStatus();
        if(byDeviceId.getDeviceStatus().equals("On")){
            String massageId = UUID.randomUUID().toString();
            turnOnLightDto.setMessageId(massageId);
            args.setCommand_name("Off");
            turnOnLightDto.setArgs(args);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(turnOnLightDto);
            String json = jsonObject.toString();
            webSocketClient.send(json);
//            operationHistoryMapper.insert("Off",userId,deviceId, LocalDateTime.now(), UUID.randomUUID().toString());
            deviceMapper.updateStatus(deviceId,"Off");
        }else {
            String massageId = UUID.randomUUID().toString();
            turnOnLightDto.setMessageId(massageId);
            args.setCommand_name("On");
            turnOnLightDto.setArgs(args);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(turnOnLightDto);
            String json = jsonObject.toString();
            System.out.println(json);
            webSocketClient.send(json);
            String operationId = UUID.randomUUID().toString();
//            operationHistoryMapper.insert("On",userId,deviceId, LocalDateTime.now(), operationId);
            deviceMapper.updateStatus(deviceId,"On");
        }
        return Result.ok("完成操作！");
    }

    @PostMapping("/adjustLight")
    public Result adjustLight(@RequestBody AdjustLightDto adjustLightDto){
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        Device byDeviceId = deviceMapper.getByDeviceId(adjustLightDto.getDeviceId());
        if(byDeviceId == null){
            return Result.error("传入了错误的id！");
        }
        if(!byDeviceId.getUserId().equals(userId)){
            return Result.error("没有操控该设备的权限！");
        }
        adjustLightDto.setCommand("device_command");
        adjustLightDto.setMessage_id(UUID.randomUUID().toString());
        Args args = new Args();
        args.setNode_id(byDeviceId.getNodeId());
        args.setEndpoint_id(byDeviceId.getEndpointId());
        args.setCluster_id(8);
        args.setCommand_name("MoveToLevelWithOnOff");
        Payload payload = new Payload();
        payload.setLevel(adjustLightDto.getLevel());
        payload.setOptionsMask(adjustLightDto.getOptionsMask());
        payload.setTransitionTime(adjustLightDto.getTransitionTime());
        payload.setOptionsOverride(adjustLightDto.getOptionsOverride());
        args.setPayload(payload);
        adjustLightDto.setArgs(args);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(adjustLightDto);
        String json = jsonObject.toString();
        log.info(json);
        webSocketClient.send(json);
        return Result.ok("调整亮度成功！");
    }

    @PostMapping("/detectCluster")
    public Result detectCluster(@RequestBody Object object){
        String deviceId = "3fcd84e5-5acf-4808-aac4-bd9cd3893a72";
        LinkedHashMap json = (LinkedHashMap) object;
        LinkedHashMap result = (LinkedHashMap) json.get("result");
        LinkedHashMap attribute = (LinkedHashMap) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        while(it.hasNext()){
            String next = it.next().toString();
            String[] split = next.split("/");
            if(split.length == 3){
                int endpoint = Integer.parseInt(split[0]);
                int clusterId = Integer.parseInt(split[1]);
                if(ClusterConst.clusterConstMap.containsKey(clusterId)){
                    deviceClusterMapper.addDeviceCluster(clusterId,endpoint,deviceId);
                }
            }
            System.out.println(it.next());
        }
        return Result.ok("Cluster扫描完成");
    }

    @PostMapping("/detectDeviceType")
    public Result detectDeviceType(@RequestBody Object object){
        LinkedHashMap json = (LinkedHashMap) object;
        LinkedHashMap result = (LinkedHashMap) ((ArrayList) json.get("result")).get(0);
        Integer DT = (Integer) result.get("DT");
        if(DT == null){
            return Result.error("设备类别为空");
        }
        if(!DeviceTypeConst.deviceConstMap.containsKey(DT)){
            return Result.error("不支持的设备！");
        }
        UpdateDeviceDto updateDeviceDto = new UpdateDeviceDto();
        updateDeviceDto.setDeviceType(DT);
        // TODO
//        updateDeviceDto.setDeviceId();
        deviceMapper.update(updateDeviceDto);
        return Result.ok("更正设备类型为：" + DT);
    }
}
