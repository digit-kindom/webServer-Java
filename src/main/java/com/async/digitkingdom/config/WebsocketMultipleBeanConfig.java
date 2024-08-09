package com.async.digitkingdom.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.common.utils.RedisCache;
import com.async.digitkingdom.entity.dto.HeartBeatDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class WebsocketMultipleBeanConfig {

    @Autowired
    private RedisCache redisCache;
    private String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjODVjMjc1ODRkYjM0MjkxYWQxNWU5NjNjNDhmNmRhMiIsImlhdCI6MTcyMDU5NzAyNywiZXhwIjoyMDM1OTU3MDI3fQ.jpqE7WwUoD1bqeT9W7Tx4qvjREgBso61dzmr7UlYpLg";

    @Bean
    public Map<String, MyWebSocketClient> websocketRunClientMap(WebSocketClientConfigurer websocketClientConfiguration) {

        Map<String, MyWebSocketClient> retMap = new HashMap<>(5);

        List<WebSocketClientConfigurer.ServerProperties> config = websocketClientConfiguration.getConfig();

        for (WebSocketClientConfigurer.ServerProperties serverProperties : config) {

            String wsUrl = serverProperties.getWsUrl();
            String wsName = serverProperties.getWsName();
            Boolean enableReconnection = serverProperties.getEnableReconnection();
            Boolean enableHeartbeat = serverProperties.getEnableHeartbeat();
            Integer heartbeatInterval = serverProperties.getHeartbeatInterval();

            try {
                MyWebSocketClient websocketRunClient = new MyWebSocketClient(new URI(wsUrl), wsName, redisCache);
                websocketRunClient.connect();
                websocketRunClient.setConnectionLostTimeout(0);

                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(heartbeatInterval);
                            if (enableHeartbeat) {
                                HeartBeatDto heartBeatDto = new HeartBeatDto("auth",accessToken);
                                JSONObject jsonObject = (JSONObject) JSON.toJSON(heartBeatDto);
                                String json = jsonObject.toString();
                                websocketRunClient.send(json);
                                log.info("[websocket {}] 心跳检测", wsName);
                            }
                        } catch (Exception e) {
                            log.error("[websocket {}] 发生异常{}", wsName, e.getMessage());
                            try {
                                if (enableReconnection) {
                                    log.info("[websocket {}] 重新连接", wsName);
                                    websocketRunClient.reconnect();
                                    websocketRunClient.setConnectionLostTimeout(0);
                                }
                            } catch (Exception ex) {
                                log.error("[websocket {}] 重连异常,{}", wsName, ex.getMessage());
                            }
                        }
                    }
                }).start();

                retMap.put(wsName, websocketRunClient);
            } catch (URISyntaxException ex) {
                log.error("[websocket {}] 连接异常,{}", wsName, ex.getMessage());
            }
        }
        return retMap;
    }

    public String generateMessageId(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
