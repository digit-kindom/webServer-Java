package com.async.digitkingdom.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {

    private String response;
    private Consumer<String> messageHandler;
    private String wsName;

    private final RedisCache redisCache;


    public MyWebSocketClient(URI serverUri, String wsName, RedisCache redisCache) {
        super(serverUri);
        this.wsName = wsName;
        this.redisCache = redisCache;
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
        JSONObject jsonObject = JSON.parseObject(response);
        String messageId = (String) jsonObject.get("message_id");
        redisCache.setCacheObject("message_id:" + messageId, response, 15, TimeUnit.MINUTES);
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }
}
