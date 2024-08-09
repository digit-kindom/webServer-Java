package com.async.digitkingdom.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.async.digitkingdom.common.*;
import com.async.digitkingdom.common.utils.MessageProcessor;
import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.common.utils.RedisCache;
import com.async.digitkingdom.entity.*;
import com.async.digitkingdom.entity.dto.*;
import com.async.digitkingdom.entity.vo.ConcentrationMeasurementVo;
import com.async.digitkingdom.entity.vo.TemperatureSensorVo;
import com.async.digitkingdom.mapper.DeviceClusterMapper;
import com.async.digitkingdom.mapper.DeviceMapper;
import com.async.digitkingdom.mapper.OperationHistoryMapper;
import com.async.digitkingdom.service.DeviceService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/device")
public class DeviceController {
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);
    @Resource
    private DeviceService deviceService;
    @Autowired
    private Map<String, MyWebSocketClient> websocketRunClientMap;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private OperationHistoryMapper operationHistoryMapper;
    @Autowired
    private DeviceClusterMapper deviceClusterMapper;
    @Autowired
    private MessageProcessor messageProcessor;
    @Resource
    private RedisCache redisCache;

//    @PostMapping("/add")
//    public Result addDevice(@RequestBody AddDeviceDto addDeviceDto) {
//        Device device = new Device();
//        BeanUtils.copyProperties(addDeviceDto, device);
//        log.info("已添加设备{}", JSON.toJSONString(device));
//        return deviceService.addDevice(device);
//    }

    @GetMapping("/sendAlert")
    public Result sendAlert() {
        return deviceService.sendAlert();
    }

    @PostMapping("/add")
    public Result addDevice(@RequestBody Object object) {
//        Device device = new Device();
//        BeanUtils.copyProperties(addDeviceDto, device);
//        log.info("已添加设备{}", JSON.toJSONString(device));
        return deviceService.addDevice(object);
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

    @PostMapping("/lightOperation/{deviceId}")
    public Result lightOperation(@PathVariable String deviceId) {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        TurnOnLightDto turnOnLightDto = new TurnOnLightDto();
        Device byDeviceId = deviceMapper.getByDeviceId(deviceId);
        if (byDeviceId == null) {
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
        if (byDeviceId.getDeviceStatus().equals("On")) {
            String massageId = UUID.randomUUID().toString();
            turnOnLightDto.setMessageId(massageId);
            args.setCommand_name("Off");
            turnOnLightDto.setArgs(args);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(turnOnLightDto);
            String json = jsonObject.toString();
            webSocketClient.send(json);
//            operationHistoryMapper.insert("Off",userId,deviceId, LocalDateTime.now(), UUID.randomUUID().toString());
            deviceMapper.updateStatus(deviceId, "Off");
        } else {
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
            deviceMapper.updateStatus(deviceId, "On");
        }
        return Result.ok("完成操作！");
    }

    @PostMapping("/adjustLight")
    public Result adjustLight(@RequestBody AdjustLightDto adjustLightDto) {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = loginUser.getUser().getId();
        Device byDeviceId = deviceMapper.getByDeviceId(adjustLightDto.getDeviceId());
        if (byDeviceId == null) {
            return Result.error("传入了错误的id！");
        }
        if (!byDeviceId.getUserId().equals(userId)) {
            return Result.error("没有操控该设备的权限！");
        }
        List<Integer> deviceClusters = deviceClusterMapper.getAllClusterByDeviceId(adjustLightDto.getDeviceId());
        if(!deviceClusters.contains(8)){
            return Result.error("该设备不支持此操作");
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
    public Result detectCluster(@RequestBody Object object) {
        LinkedHashMap json = (LinkedHashMap) object;
        LinkedHashMap result = (LinkedHashMap) json.get("result");
        LinkedHashMap attribute = (LinkedHashMap) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        ConcentrationMeasurementVo concentrationMeasurementVo = new ConcentrationMeasurementVo();
        while (it.hasNext()) {
            String next = it.next().toString();
            String[] split = next.split("/");
            if (split.length == 3) {
                int endpoint = Integer.parseInt(split[0]);
                int clusterId = Integer.parseInt(split[1]);
                if (ClusterConst.clusterConstMap.containsKey(clusterId) && clusterId == 1037) {
                    String[] value = split[2].split("=");
                    if (ConcentrationMeasurementConst.attributes.containsKey(Integer.parseInt(value[0]))) {
                        String attributeName = ConcentrationMeasurementConst.attributes.get(Integer.parseInt(value[0]));
                        if(attributeName == null) continue;
                        if (attributeName.equals("MeasuredValue")) {
                            concentrationMeasurementVo.setMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MinMeasuredValue")) {
                            concentrationMeasurementVo.setMinMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MaxMeasuredValue")) {
                            concentrationMeasurementVo.setMaxMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("PeakMeasuredValue")) {
                            concentrationMeasurementVo.setPeakMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("PeakMeasuredValueWindow")){
                            concentrationMeasurementVo.setPeakMeasuredValueWindow(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("AverageMeasuredValue")){
                            concentrationMeasurementVo.setAverageMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("AverageMeasuredValueWindow")){
                            concentrationMeasurementVo.setAverageMeasuredValueWindow(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("Uncertainty")){
                            concentrationMeasurementVo.setUncertainty(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MeasurementUnit")){
                            concentrationMeasurementVo.setMeasurementUnit(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MeasurementMedium")){
                            concentrationMeasurementVo.setMeasurementMedium(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("LevelValue")){
                            concentrationMeasurementVo.setLevelValue(Float.valueOf(value[1]));
                            continue;
                        }
                    }
                }
            }
        }
        return Result.ok(concentrationMeasurementVo);
    }

    @PostMapping("/detectDeviceType")
    public Result detectDeviceType(@RequestBody Object object) {
        LinkedHashMap json = (LinkedHashMap) object;
        LinkedHashMap result = (LinkedHashMap) ((ArrayList) json.get("result")).get(0);
        Integer DT = (Integer) result.get("DT");
        if (DT == null) {
            return Result.error("设备类别为空");
        }
        if (!DeviceTypeConst.deviceConstMap.containsKey(DT)) {
            return Result.error("不支持的设备！");
        }
        UpdateDeviceDto updateDeviceDto = new UpdateDeviceDto();
        updateDeviceDto.setDeviceType(DT);
//        updateDeviceDto.setDeviceId();
        deviceMapper.update(updateDeviceDto);
        return Result.ok("更正设备类型为：" + DT);
    }

//    @GetMapping("/status/{deviceId}")
//    public Result getSingleDeviceStatus(@PathVariable String deviceId) {
//        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Integer userId = loginUser.getUser().getId();
//        Device device = deviceMapper.getByDeviceId(deviceId);
//        if (device == null) {
//            return Result.error("输入了错误的设备id");
//        }
//        if(!device.getUserId().equals(loginUser.getUser().getId())){
//            return Result.error("没有操作权限");
//        }
//
//    }

    @GetMapping("/generateQrcode")
    public void generateQRCode(String deviceId, HttpServletResponse resp) {
        MyWebSocketClient webSocketClient2 = websocketRunClientMap.get("ws-02");
        long id = System.currentTimeMillis() - 656446;
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjODVjMjc1ODRkYjM0MjkxYWQxNWU5NjNjNDhmNmRhMiIsImlhdCI6MTcyMDU5NzAyNywiZXhwIjoyMDM1OTU3MDI3fQ.jpqE7WwUoD1bqeT9W7Tx4qvjREgBso61dzmr7UlYpLg";
        AuthDto authDto = new AuthDto("auth", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjODVjMjc1ODRkYjM0MjkxYWQxNWU5NjNjNDhmNmRhMiIsImlhdCI6MTcyMDU5NzAyNywiZXhwIjoyMDM1OTU3MDI3fQ.jpqE7WwUoD1bqeT9W7Tx4qvjREgBso61dzmr7UlYpLg");
        JSONObject jsonObject = (JSONObject) JSON.toJSON(authDto);
        String json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String latestMessage = webSocketClient2.getResponse();
//        JSONObject jsonObject1 = JSONObject.parseObject(latestMessage);
//        String status = (String) jsonObject1.get("type");
//        if (!status.equals("auth_ok")) {
//            return;
//        }

        PersistentNotificationDto persistentNotificationDto = new PersistentNotificationDto("persistent_notification/subscribe", id);
        jsonObject = (JSONObject) JSON.toJSON(persistentNotificationDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();
        id++;

        ConfigEntriesDto configEntriesDto = new ConfigEntriesDto("config_entries/flow/progress", id);
        jsonObject = (JSONObject) JSON.toJSON(configEntriesDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();
        id++;

        ManifestDto manifestDto = new ManifestDto("manifest/get", "homekit", id);
        jsonObject = (JSONObject) JSON.toJSON(manifestDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();
        id++;

        List integration = new ArrayList<>();
        integration.add("homekit");
        FrontendDto frontendDto = new FrontendDto("frontend/get_translations", "zh-Hans", "config", integration, id);
        jsonObject = (JSONObject) JSON.toJSON(frontendDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();
        id++;

        frontendDto.setCategory("selector");
        frontendDto.setId(id);
        jsonObject = (JSONObject) JSON.toJSON(frontendDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();
        id++;


        HandlerDto handlerDto = new HandlerDto("homekit", false);
        jsonObject = (JSONObject) JSON.toJSON(handlerDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);

        OkHttpClient client = new OkHttpClient();
        // Creating the request
        Request request = new Request.Builder()
                .url("http://192.168.1.131:8123/api/config/config_entries/flow")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // Making the request and getting the response
        String flowId = null;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Printing the response
//            System.out.println(response.body().string());
            String string = response.body().string();
            JSONObject jsonObject2 = JSONObject.parseObject(string);
            flowId = (String) jsonObject2.get("flow_id");

        } catch (IOException e) {
            e.printStackTrace();
        }

        IncludeDomainDto includeDomainDto = new IncludeDomainDto();
        List<String> include_domains = new ArrayList<>();
        include_domains.add("alarm_control_panel");
        include_domains.add("climate");
        include_domains.add("camera");
        include_domains.add("cover");
        include_domains.add("humidifier");
        include_domains.add("fan");
        include_domains.add("light");
        include_domains.add("lock");
        include_domains.add("media_player");
        include_domains.add("remote");
        include_domains.add("switch");
        include_domains.add("vacuum");
        include_domains.add("water_heater");
        include_domains.add("binary_sensor");
        include_domains.add("sensor");
        include_domains.add("button");
        includeDomainDto.setInclude_domains(include_domains);

        jsonObject = (JSONObject) JSON.toJSON(includeDomainDto);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        body = okhttp3.RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request2 = new Request.Builder()
                .url("http://192.168.1.131:8123/api/config/config_entries/flow/" + flowId)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // Making the request and getting the response
        try (Response response = client.newCall(request2).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Printing the response
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        PersistentNotificationDto persistentNotificationDto1 = new PersistentNotificationDto("config/device_registry/list", id);
        jsonObject = (JSONObject) JSON.toJSON(persistentNotificationDto1);
        json = jsonObject.toString();
        System.out.println("---------");
        System.out.println(json);
        System.out.println("---------");
        webSocketClient2.send(json);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();

        body = okhttp3.RequestBody.create(MediaType.get("application/json; charset=utf-8"), "{}");
        Request request3 = new Request.Builder()
                .url("http://192.168.1.131:8123/api/config/config_entries/flow/" + flowId)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // Making the request and getting the response
        try (Response response = client.newCall(request3).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Printing the response
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        latestMessage = webSocketClient2.getResponse();

        String[] split = latestMessage.split("\\(");
        String[] split1 = split[1].split("\\)");
        String url = "http://192.168.1.131:8123" + split1[0];

        Request request4 = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request4).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            byte[] bytes = response.body().bytes();
            resp.setContentType("image/svg+xml");
            resp.setContentLength(bytes.length);

            try(OutputStream outputStream = resp.getOutputStream()) {
                outputStream.write(bytes);
            }
        } catch (IOException e) {
            return;
        }
    }

    @GetMapping("/receiveImageFromCamera")
    public void receiveImageFromCamera(HttpServletResponse res){

        ServletOutputStream out = null;

        res.setContentType("image/jpeg");
        try {
            out = res.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.162.159:81/stream")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Printing the response
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            URL url = new URL("192.168.162.159:81/stream");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn.getResponseCode() == 200){
                InputStream is = conn.getInputStream();

                byte[] buffer = new byte[1024];
                int len = 0;

                while((len = is.read(buffer)) > 0){
                    out.write(buffer,0,len);
                }

                out.close();
                is.close();
            }
        }catch (Exception e){
            return;
        }
    }

    @GetMapping("/updateCameraFrameSize")
    public Result updateCameraFramesize(@RequestParam int frameSize){

        if(frameSize < 0 || frameSize > 13){
            return Result.error("framesize must be between 0 and 13");
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.50.21/control?var=framesize&val=" + frameSize)
                .get().build();

        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful()){
                return Result.ok("更新分辨率成功！");
            }else {
                log.error(response.body().toString());
                return Result.error("更新分辨率时出现未知异常！");
            }
        } catch (IOException e) {
            return Result.error("更新分辨率时出现未知异常！");
        }
    }

//
//    public Object queryDeviceStatus(Integer nodeId){
//        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
//        QueryDeviceStatusDto queryDeviceStatusDto = new QueryDeviceStatusDto();
//        queryDeviceStatusDto.setNode_id(nodeId);
//        queryDeviceStatusDto.setCommand("read_attribute");
//        queryDeviceStatusDto.setMessageId(UUID.randomUUID().toString());
//        queryDeviceStatusDto.setAttribute_path("");
//        JSONObject jsonObject = (JSONObject) JSON.toJSON(queryDeviceStatusDto);
//        String json = jsonObject.toString();
//
//        webSocketClient.send(json);
//
//        String latestMessage = messageProcessor.getLatestMessage();
//
//        return latestMessage;
//    }

    @GetMapping("/getTemperatureSensorStatus")
    public Result getTemperatureSensorStatus (Integer nodeId) throws InterruptedException {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        Args args = new Args();
        args.setNode_id(nodeId);
        String messageId = generateMessageId();
        GetNode getNode = new GetNode("get_node",args,messageId);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(getNode);
        String json = jsonObject.toString();

        webSocketClient.send(json);

        String receiveData = null;
        long startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("获取温度传感器数据失败！");
            }
            Thread.sleep(500);
        }
        redisCache.deleteObject("message_id:" + messageId);

        JSONObject jsonObject1 = JSON.parseObject(receiveData);
        JSONObject result = (JSONObject) jsonObject1.get("result");
        JSONObject attribute = (JSONObject) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        TemperatureSensorVo temperatureSensorVo = new TemperatureSensorVo();
        while (it.hasNext()) {
            String next = it.next().toString();
            String[] split = next.split("/");
            if (split.length == 3) {
                int endpoint = Integer.parseInt(split[0]);
                int clusterId = Integer.parseInt(split[1]);
                if (ClusterConst.clusterConstMap.containsKey(clusterId)) {
                    String[] value = split[2].split("=");
                    if (TemperatureSensorConst.attributes.containsKey(Integer.parseInt(value[0]))) {
                        String attributeName = TemperatureSensorConst.attributes.get(Integer.parseInt(value[0]));
                        if (attributeName.equals("MeasuredValue")) {
                            temperatureSensorVo.setMeasuredValue(Integer.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MinMeasuredValue")) {
                            temperatureSensorVo.setMinMeasuredValue(Integer.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MaxMeasuredValue")) {
                            temperatureSensorVo.setMaxMeasuredValue(Integer.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("Tolerance")) {
                            temperatureSensorVo.setTolerance(Integer.valueOf(value[1]));
                            continue;
                        }
                    }
                }
            }
        }
        return Result.ok(temperatureSensorVo);
    }

    @GetMapping("/getAirCondition")
    public Result getAirCondition (Integer nodeId, Integer targetClusterId) throws InterruptedException {
        if (!AirConditionConst.airConditionCluster.containsKey(targetClusterId)) {
            return Result.error("传入了错误的clusterId");
        }
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        Args args = new Args();
        args.setNode_id(nodeId);
        String messageId = generateMessageId();
        GetNode getNode = new GetNode("get_node", args, messageId);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(getNode);
        String json = jsonObject.toString();

        webSocketClient.send(json);
//        String latestMessage = webSocketClient.getResponse();
        String receiveData = null;
        long startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("获取空气传感器数据失败！");
            }
            Thread.sleep(500);
        }
        redisCache.deleteObject("message_id:" + messageId);

        JSONObject jsonObject1 = JSON.parseObject(receiveData);
        JSONObject result = (JSONObject) jsonObject1.get("result");
        JSONObject attribute = (JSONObject) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        ConcentrationMeasurementVo concentrationMeasurementVo = new ConcentrationMeasurementVo();
        while (it.hasNext()) {
            String next = it.next().toString();
            String[] split = next.split("/");
            if (split.length == 3) {
                int endpoint = Integer.parseInt(split[0]);
                int clusterId = Integer.parseInt(split[1]);
                if (AirConditionConst.airConditionCluster.containsKey(clusterId) && targetClusterId == clusterId) {
                    String[] value = split[2].split("=");
                    if (ConcentrationMeasurementConst.attributes.containsKey(Integer.parseInt(value[0]))) {
                        String attributeName = ConcentrationMeasurementConst.attributes.get(Integer.parseInt(value[0]));
                        if (attributeName == null) continue;
                        if (attributeName.equals("MeasuredValue")) {
                            concentrationMeasurementVo.setMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MinMeasuredValue")) {
                            concentrationMeasurementVo.setMinMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MaxMeasuredValue")) {
                            concentrationMeasurementVo.setMaxMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("PeakMeasuredValue")) {
                            concentrationMeasurementVo.setPeakMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("PeakMeasuredValueWindow")) {
                            concentrationMeasurementVo.setPeakMeasuredValueWindow(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("AverageMeasuredValue")) {
                            concentrationMeasurementVo.setAverageMeasuredValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("AverageMeasuredValueWindow")) {
                            concentrationMeasurementVo.setAverageMeasuredValueWindow(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("Uncertainty")) {
                            concentrationMeasurementVo.setUncertainty(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MeasurementUnit")) {
                            concentrationMeasurementVo.setMeasurementUnit(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MeasurementMedium")) {
                            concentrationMeasurementVo.setMeasurementMedium(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("LevelValue")) {
                            concentrationMeasurementVo.setLevelValue(Float.valueOf(value[1]));
                            continue;
                        }
                    }
                }
            }
        }
        concentrationMeasurementVo.setTarget(AirConditionConst.airConditionCluster.get(targetClusterId));
        return Result.ok(concentrationMeasurementVo);
    }


    public String generateMessageId(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}

