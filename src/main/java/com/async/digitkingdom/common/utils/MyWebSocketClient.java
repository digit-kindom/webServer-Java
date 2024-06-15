package com.async.digitkingdom.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.info("------ WebSocketClient onOpen ------");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.info("------ WebSocket onClose ------{}",arg1);
    }

    @Override
    public void onError(Exception arg0) {
        log.error("------ WebSocket onError ------{}",arg0);
    }

    @Override
    public void onMessage(String response) {
        log.info("-------- 接收到服务端数据： " + response + "--------");
    }

}
