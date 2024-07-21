package com.async.digitkingdom.config;

import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.common.utils.MessageProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "digit.websocket.client")
public class WebSocketClientConfigurer {

//    private final String wsServerUrl1 = "ws://192.168.2.131:5580/ws";
//    private final String wsServerUrl2 = "ws://192.168.2.131:8123/api/websocket";
//
//    @Autowired
//    private MessageProcessor messageProcessor;
//
//    @Bean
//    public WebSocketClient webSocketClient() {
//        try {
//            MyWebSocketClient webSocketClient = new MyWebSocketClient(new URI(wsServerUrl1), messageProcessor::process);
//            webSocketClient.connect();
//            return webSocketClient;
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Bean
//    public WebSocketClient webSocketClient2() {
//        try {
//            MyWebSocketClient webSocketClient = new MyWebSocketClient(new URI(wsServerUrl2), messageProcessor::process);
//            webSocketClient.connect();
//            return webSocketClient;
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private List<ServerProperties> config;

    public static class ServerProperties {
        /**
         * websocket server ws://ip:port
         */
        private String wsUrl;
        /**
         * websocket server name,用于区分不同的服务端
         */
        private String wsName;
        /**
         * 是否启用心跳监测 默认开启
         */
        private Boolean enableHeartbeat;
        /**
         * 心跳监测间隔 默认20000毫秒
         */
        private Integer heartbeatInterval;
        /**
         * 是否启用重连接 默认启用
         */
        private Boolean enableReconnection;

        public String getWsUrl() {
            return wsUrl;
        }

        public void setWsUrl(String wsUrl) {
            this.wsUrl = wsUrl;
        }

        public Boolean getEnableHeartbeat() {
            return enableHeartbeat;
        }

        public void setEnableHeartbeat(Boolean enableHeartbeat) {
            this.enableHeartbeat = enableHeartbeat;
        }

        public Integer getHeartbeatInterval() {
            return heartbeatInterval;
        }

        public void setHeartbeatInterval(Integer heartbeatInterval) {
            this.heartbeatInterval = heartbeatInterval;
        }

        public Boolean getEnableReconnection() {
            return enableReconnection;
        }

        public void setEnableReconnection(Boolean enableReconnection) {
            this.enableReconnection = enableReconnection;
        }

        public String getWsName() {
            return wsName;
        }

        public void setWsName(String wsName) {
            this.wsName = wsName;
        }
    }

    public List<ServerProperties> getConfig() {
        return config;
    }

    public void setConfig(List<ServerProperties> config) {
        this.config = config;
    }

}
