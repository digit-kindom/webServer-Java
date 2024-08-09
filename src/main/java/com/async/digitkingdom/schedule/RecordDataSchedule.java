package com.async.digitkingdom.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.async.digitkingdom.common.ConcentrationMeasurementConst;
import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.common.utils.RedisCache;
import com.async.digitkingdom.entity.Args;
import com.async.digitkingdom.entity.dto.AirQualityHistoryDto;
import com.async.digitkingdom.entity.dto.GetNode;
import com.async.digitkingdom.entity.vo.ConcentrationMeasurementVo;
import com.async.digitkingdom.mapper.AirQualitySensorHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

@Component
@EnableScheduling
public class RecordDataSchedule {
    private static final Logger log = LoggerFactory.getLogger(RecordDataSchedule.class);
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private Map<String, MyWebSocketClient> websocketRunClientMap;
    @Autowired
    private AirQualitySensorHistoryMapper airQualitySensorHistory;
    @Autowired
    private AirQualitySensorHistoryMapper airQualitySensorHistoryMapper;


    @Scheduled(cron = "0 * * * * ?")
    public void recordCO2() throws InterruptedException {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        Args args = new Args();
        args.setNode_id(117);
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
        AirQualityHistoryDto airQualityHistoryDto = new AirQualityHistoryDto();
        while (it.hasNext()) {
            String next = it.next().toString();
            String[] split = next.split("/");
            if (split.length == 3) {
                    String[] value = split[2].split("=");
                    if (Integer.parseInt(split[1]) == 1037) {
                        String attributeName = ConcentrationMeasurementConst.attributes.get(Integer.parseInt(value[0]));
                        if (attributeName == null) continue;
                        if (attributeName.equals("MeasuredValue")) {
                            airQualityHistoryDto.setValue(Float.valueOf(value[1]));
                            continue;
                        }
                        if (attributeName.equals("MeasurementUnit")) {
                            airQualityHistoryDto.setUnit(Float.valueOf(value[1]));
                            continue;
                        }
                    }
            }
        }
        airQualityHistoryDto.setSubstanceType("1037");
        airQualityHistoryDto.setNodeId(117);
        airQualityHistoryDto.setDetectTime(LocalDateTime.now());
        airQualitySensorHistoryMapper.insert(airQualityHistoryDto);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void recordTVOC() throws InterruptedException {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        Args args = new Args();
        args.setNode_id(117);
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
                throw new RuntimeException("获取TVOC数据失败！");
            }
            Thread.sleep(500);
        }
        redisCache.deleteObject("message_id:" + messageId);

        JSONObject jsonObject1 = JSON.parseObject(receiveData);
        JSONObject result = (JSONObject) jsonObject1.get("result");
        JSONObject attribute = (JSONObject) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        AirQualityHistoryDto airQualityHistoryDto = new AirQualityHistoryDto();
        while (it.hasNext()) {
            String next = it.next().toString();
            String[] split = next.split("/");
            if (split.length == 3) {
                String[] value = split[2].split("=");
                if (Integer.parseInt(split[1]) == 1070) {
                    String attributeName = ConcentrationMeasurementConst.attributes.get(Integer.parseInt(value[0]));
                    if (attributeName == null) continue;
                    if (attributeName.equals("MeasuredValue")) {
                        airQualityHistoryDto.setValue(Float.valueOf(value[1]));
                        continue;
                    }
                    if (attributeName.equals("MeasurementUnit")) {
                        airQualityHistoryDto.setUnit(Float.valueOf(value[1]));
                        continue;
                    }
                }
            }
        }
        airQualityHistoryDto.setSubstanceType("1070");
        airQualityHistoryDto.setNodeId(117);
        airQualityHistoryDto.setDetectTime(LocalDateTime.now());
        airQualitySensorHistoryMapper.insert(airQualityHistoryDto);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void recordTemperature() throws InterruptedException {
        MyWebSocketClient webSocketClient = websocketRunClientMap.get("ws-01");
        Args args = new Args();
        args.setNode_id(117);
        String messageId = generateMessageId();
        GetNode getNode = new GetNode("get_node", args, messageId);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(getNode);
        String json = jsonObject.toString();

        try {
            webSocketClient.send(json);
        }catch (Exception e){
            if(e.toString().contains("null")){
                log.error("Websocket 未连接！");
                return;
            }else {
                log.error("获取传感器数据时发生未知错误！");
                return;
            }
        }

//        String latestMessage = webSocketClient.getResponse();
        String receiveData = null;
        long startTime = System.currentTimeMillis();
        while (receiveData == null) {
            receiveData = redisCache.getCacheObject("message_id:" + messageId);
            if(System.currentTimeMillis() - startTime > 5000){
                throw new RuntimeException("获取温度数据失败！");
            }
            Thread.sleep(500);
        }
        redisCache.deleteObject("message_id:" + messageId);

        JSONObject jsonObject1 = JSON.parseObject(receiveData);
        JSONObject result = (JSONObject) jsonObject1.get("result");
        JSONObject attribute = (JSONObject) result.get("attributes");
        Iterator it = attribute.entrySet().iterator();
        HashSet<Integer> set = new HashSet<Integer>();
        AirQualityHistoryDto airQualityHistoryDto = new AirQualityHistoryDto();
        while (it.hasNext()) {
            String next = it.next().toString();
            String[] split = next.split("/");
            if (split.length == 3) {
                String[] value = split[2].split("=");
                if (Integer.parseInt(split[1]) == 1026) {
                    String attributeName = ConcentrationMeasurementConst.attributes.get(Integer.parseInt(value[0]));
                    if (attributeName == null) continue;
                    if (attributeName.equals("MeasuredValue")) {
                        airQualityHistoryDto.setValue(Float.valueOf(value[1]));
                        continue;
                    }
                    if (attributeName.equals("MeasurementUnit")) {
                        airQualityHistoryDto.setUnit(Float.valueOf(value[1]));
                        continue;
                    }
                }
            }
        }
        airQualityHistoryDto.setSubstanceType("1026");
        airQualityHistoryDto.setNodeId(117);
        airQualityHistoryDto.setDetectTime(LocalDateTime.now());
        airQualitySensorHistoryMapper.insert(airQualityHistoryDto);
    }

    public String generateMessageId(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
