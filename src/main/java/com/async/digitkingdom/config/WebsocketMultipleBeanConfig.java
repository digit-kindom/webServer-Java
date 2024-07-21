package com.async.digitkingdom.config;

import com.async.digitkingdom.common.utils.MyWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class WebsocketMultipleBeanConfig {

    @Bean
    public Map<String, MyWebSocketClient> websocketRunClientMap(WebSocketClientConfigurer websocketClientConfiguration){

        Map<String, MyWebSocketClient> retMap = new HashMap<>(5);

        List<WebSocketClientConfigurer.ServerProperties> config = websocketClientConfiguration.getConfig();

        for (WebSocketClientConfigurer.ServerProperties serverProperties : config) {

            String wsUrl = serverProperties.getWsUrl();
            String wsName = serverProperties.getWsName();
            Boolean enableReconnection = serverProperties.getEnableReconnection();
            Boolean enableHeartbeat = serverProperties.getEnableHeartbeat();
            Integer heartbeatInterval = serverProperties.getHeartbeatInterval();

            try {
                MyWebSocketClient websocketRunClient = new MyWebSocketClient(new URI(wsUrl),wsName);
                websocketRunClient.connect();
                websocketRunClient.setConnectionLostTimeout(0);

//                new Thread(()->{
//                    while (true){
//                        try {
//                            Thread.sleep(heartbeatInterval);
//                            if(enableHeartbeat){
//                                websocketRunClient.send("[websocket "+wsName+"] 心跳检测");
//                                log.info("[websocket {}] 心跳检测",wsName);
//                            }
//                        } catch (Exception e) {
//                            log.error("[websocket {}] 发生异常{}",wsName,e.getMessage());
//                            try {
//                                if(enableReconnection){
//                                    log.info("[websocket {}] 重新连接",wsName);
//                                    websocketRunClient.reconnect();
//                                    websocketRunClient.setConnectionLostTimeout(0);
//                                }
//                            }catch (Exception ex){
//                                log.error("[websocket {}] 重连异常,{}",wsName,ex.getMessage());
//                            }
//                        }
//                    }
//                }).start();

                retMap.put(wsName,websocketRunClient);
            } catch (URISyntaxException ex) {
                log.error("[websocket {}] 连接异常,{}",wsName,ex.getMessage());
            }
        }
        return retMap;

    }

}

