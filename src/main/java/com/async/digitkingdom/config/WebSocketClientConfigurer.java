package com.async.digitkingdom.config;

import com.async.digitkingdom.common.utils.MyWebSocketClient;
import com.async.digitkingdom.common.utils.MessageProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class WebSocketClientConfigurer {

    private final String wsServerUrl1 = "ws://192.168.2.131:5580/ws";
    private final String wsServerUrl2 = "ws://192.168.2.131:8123/api/websocket";

    @Autowired
    private MessageProcessor messageProcessor;

    @Bean
    public WebSocketClient webSocketClient() {
        try {
            MyWebSocketClient webSocketClient = new MyWebSocketClient(new URI(wsServerUrl1), messageProcessor::process);
            webSocketClient.connect();
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public WebSocketClient webSocketClient2() {
        try {
            MyWebSocketClient webSocketClient = new MyWebSocketClient(new URI(wsServerUrl2), messageProcessor::process);
            webSocketClient.connect();
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
