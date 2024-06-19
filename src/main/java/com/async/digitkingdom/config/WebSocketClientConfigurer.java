package com.async.digitkingdom.config;

import com.async.digitkingdom.common.utils.MyWebSocketClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class WebSocketClientConfigurer {

    private final String wsServerUrl = "ws://192.168.118.177:5580/ws";

    @Bean
    public WebSocketClient webSocketClient() {
        try {
            MyWebSocketClient webSocketClient = new MyWebSocketClient(new URI(wsServerUrl));
            webSocketClient.connect();
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
