package com.async.digitkingdom.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.async.digitkingdom.common.DeviceTypeConst;
import com.async.digitkingdom.common.Result;
import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.common.utils.RedisCache;
import com.async.digitkingdom.entity.*;
import com.async.digitkingdom.entity.dto.*;
import com.async.digitkingdom.mapper.DeviceClusterMapper;
import com.async.digitkingdom.mapper.DeviceMapper;
import com.async.digitkingdom.service.DeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DeviceClusterMapper deviceClusterMapper;
    @Autowired
    private Map<String, MyWebSocketClient> websocketRunClientMap;
    @Resource
    private RedisCache redisCache;

//    @Override
//    public Result addDevice(Device device) {
//        device.setCreateDate(LocalDate.now());
//        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        device.setUserId(user.getUser().getId());
//        device.setDeviceId(UUID.randomUUID().toString());
//        deviceMapper.insert(device);
//        return Result.ok("添加设备成功！");
//    }

    @Override
    @Transactional
    public Result addDevice(Object object) {
        String deviceId = UUID.randomUUID().toString();
        Device device = detectDeviceJson(object, deviceId);
        if(device.getDeviceType() == null){
            return Result.error("未检测到设备类型或不支持的设备类型");
        }
        device.setCreateDate(LocalDate.now());
        LoginUser user = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        device.setUserId(user.getUser().getId());
        device.setDeviceId(deviceId);
        device.setDeviceName(DeviceTypeConst.deviceConstMap.get(device.getDeviceType()));
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

    @Override
    public Result receiveImageFromCamera(int frameSize) {
        return null;
    }

    @Override
    public Result sendAlert() {
//        deviceMapper.alertList();
        return Result.ok();
    }

    @Override
    public Result operateFan(Integer nodeId, Integer value) {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        Args args = new Args();
        args.setNode_id(nodeId);
        String messageId = generateMessageId();
        GetNode getNode = new GetNode("get_node", args, messageId);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(getNode);
        String json = jsonObject.toString();
        webSocketClient.send(json);

        String receiveData = null;
        long startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("获取风扇数据失败！");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        redisCache.deleteObject("message_id:" + messageId);

        JSONObject jsonObject1 = JSON.parseObject(receiveData);
        JSONObject result = (JSONObject) jsonObject1.get("result");
        JSONObject attribute = (JSONObject) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        String attributePath = null;

        while (it.hasNext()) {
            String next = it.next().toString();
            if(next.split("=")[0].contains("514")){
                attributePath = next.split("=")[0];
                break;
            }
        }

        if(attributePath == null){
            return Result.error("不支持该设备类型");
        }

        String[] split = attributePath.split("/");
        attributePath = split[0] + "/514/" + "5";

        messageId = generateMessageId();
        OperateFanDto operateFanDto = new OperateFanDto();
        operateFanDto.setMessage_id(messageId);
        operateFanDto.setCommand("write_attribute");
        operateFanDto.setArgs(new OperateFanArgs(nodeId,attributePath,value));

        jsonObject = (JSONObject) JSON.toJSON(operateFanDto);
        json = jsonObject.toString();
        webSocketClient.send(json);

        receiveData = null;
        startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("操作风扇失败");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        redisCache.deleteObject("message_id:" + messageId);
        if(receiveData.contains("0")){
            return Result.ok("操作成功！");
        }else {
            return Result.error("操作异常！");
        }
    }

    @Override
    public Result operateColorfulLight(Integer nodeId, Integer value) {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");

        Args args = new Args();
        args.setNode_id(nodeId);
        String messageId = generateMessageId();
        GetNode getNode = new GetNode("get_node", args, messageId);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(getNode);
        String json = jsonObject.toString();
        webSocketClient.send(json);

        String receiveData = null;
        long startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("获取彩灯数据失败！");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        redisCache.deleteObject("message_id:" + messageId);

        JSONObject jsonObject1 = JSON.parseObject(receiveData);
        JSONObject result = (JSONObject) jsonObject1.get("result");
        JSONObject attribute = (JSONObject) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        String attributePath = null;

        while (it.hasNext()) {
            String next = it.next().toString();
            if(next.split("=")[0].contains("80")){
                attributePath = next.split("=")[0];
                break;
            }
        }

        if(attributePath == null){
            return Result.error("不支持该设备类型");
        }

        String[] split = attributePath.split("/");
        String endpoint = split[0];


        messageId = generateMessageId();
        OperateColorfulLightDto operateColorfulLightDto = new OperateColorfulLightDto(
                messageId,
                "device_command",
                new OperateColorfulLightArgs(Integer.parseInt(endpoint),nodeId,
                        new ColorfulLightPayload(value)),
                80,
                "ChangeToMode"
        );

        jsonObject = (JSONObject) JSON.toJSON(operateColorfulLightDto);
        json = jsonObject.toString();
        webSocketClient.send(json);

        receiveData = null;
        startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("操作风扇失败");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        redisCache.deleteObject("message_id:" + messageId);

        return Result.ok("操作成功！");
    }

//    public Result detectCluster(Object object) {
////        String deviceId = "3fcd84e5-5acf-4808-aac4-bd9cd3893a72";
//        LinkedHashMap json = (LinkedHashMap) object;
//        LinkedHashMap result = (LinkedHashMap) json.get("result");
//        LinkedHashMap attribute = (LinkedHashMap) result.get("attributes");
//        Iterator it = attribute.entrySet().iterator();
//        while (it.hasNext()) {
//            String next = it.next().toString();
//            String[] split = next.split("/");
//            if (split.length == 3) {
//                int endpoint = Integer.parseInt(split[0]);
//                int clusterId = Integer.parseInt(split[1]);
//                if (ClusterConst.clusterConstMap.containsKey(clusterId)) {
////                    deviceClusterMapper.addDeviceCluster(clusterId, endpoint, deviceId);
//                }
//            }
//            System.out.println(it.next());
//        }
//        return Result.ok("Cluster扫描完成");
//    }
//
//    public Integer detectDeviceType(Object object, String deviceId) {
//        LinkedHashMap json = (LinkedHashMap) object;
//        LinkedHashMap result = (LinkedHashMap) ((ArrayList) json.get("result")).get(0);
//        Integer DT = (Integer) result.get("DT");
//        if (DT == null) {
//            return null;
//        }
//        if (!DeviceTypeConst.deviceConstMap.containsKey(DT)) {
//            return null;
//        }
//        UpdateDeviceDto updateDeviceDto = new UpdateDeviceDto();
//        updateDeviceDto.setDeviceType(DT);
//        updateDeviceDto.setDeviceId(deviceId);
//        deviceMapper.update(updateDeviceDto);
//        return DT;
//    }

    public Device detectDeviceJson(Object object, String deviceId) {
        Device device = new Device();
        LinkedHashMap json = (LinkedHashMap) object;
        LinkedHashMap result = (LinkedHashMap) json.get("result");
        // nodeId 获取
        Integer nodeId = (Integer) result.get("node_id");
        // qrcode 获取
        String qrcode = (String) json.get("qrcode");
        // cluster endpoint 检测
//        LinkedHashMap attribute = (LinkedHashMap) result.get("attributes");
//        Iterator it = attribute.entrySet().iterator();
//        while (it.hasNext()) {
//            String next = it.next().toString();
//            String[] split = next.split("/");
//            if (split.length == 3) {
//                int endpoint = Integer.parseInt(split[0]);
//                int clusterId = Integer.parseInt(split[1]);
//                if (ClusterConst.clusterConstMap.containsKey(clusterId)) {
//                    deviceClusterMapper.addDeviceCluster(clusterId, endpoint, deviceId);
//                }
//            }
//        }

        // DeviceType 检测
        String DTstr = (String) json.get("dT");
//        String[] split = DTstr.split("x");
        Integer DT = null;
        DT = Integer.parseInt(DTstr);
//        int i = Integer.parseInt(DTstr, 16);
//        if(split.length == 2){
//            DT = Integer.parseInt(split[1],16);
//        }else {
//            DT = Integer.parseInt(DTstr);
//        }

        if (DT == null) {
            return null;
        }
        if (!DeviceTypeConst.deviceConstMap.containsKey(DT)) {
            return null;
        }
        if (qrcode == null || qrcode.length() == 0) {
            return null;
        }

        device.setQrcode(qrcode);
        device.setNodeId(nodeId);
        device.setDeviceType(DT);
        return device;
    }

    public String generateMessageId(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
