package com.async.digitkingdom.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    private String response;
    private Consumer<String> messageHandler;
    private String wsName;


    public MyWebSocketClient(URI serverUri, String wsName) {
        super(serverUri);
    }

//    public MyWebSocketClient(URI serverUri, Consumer<String> messageHandler) {
//        super(serverUri);
//        this.message  Handler = messageHandler;
//    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.info("------ WebSocketClient onOpen ------");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.info("------ WebSocket onClose ------{}", arg1);
    }

    @Override
    public void onError(Exception arg0) {
        log.error("------ WebSocket onError ------{}", arg0);
    }

    @Override
    public void onMessage(String response) {
        log.info("-------- 接收到服务端数据： " + response + "--------");
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }
}
