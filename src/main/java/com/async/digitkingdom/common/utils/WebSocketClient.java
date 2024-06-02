package com.async.digitkingdom.common.utils;

import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnError;
import javax.websocket.Session;

@Component
public class WebSocketClient extends WebSocketListener {
    @Autowired
    private WebSocketServer webSocketServer;

    private WebSocket webSocket;

    // 客户端连接其他服务器
    public void connectToServer(String serverUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(serverUrl).build();
        webSocket = okHttpClient.newWebSocket(request, this);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
//        ResponseData responseData = GSON.fromJson(text, ResponseData.class);
//        //此处服务器返回的status值为0时代表连接正常，由接口具体情况而定，与协议无关
//        if (0 == responseData.getHeader().get("code").getAsInt()) {
//            Payload pl =GSON.fromJson(responseData.getPayload(), Payload.class);
//            JsonArray temp = (JsonArray) pl.getChoices().get("text");
//            JsonObject jo = (JsonObject) temp.get(0);
//            //解析结果后将内容转发给下游客户端，也可以使用sendMessage方法定向发送
//            webSocketServer.broadcast(jo.get("content").getAsString());
//            //如果不想每次发送消息时都主动连接，需要建立websocket心跳，这里每次收发消息都主动断开
//            webSocket.close(3, "客户端主动断开链接");
//        } else {
//            System.out.println("返回结果错误：\n" + responseData.getHeader().get("code") + " " + responseData.getHeader().get("message"));
//        }
        super.onMessage(webSocket, text);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("WebSocket连接失败：");
        super.onFailure(webSocket, t, response);
        System.out.println(response);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket发生错误：" + throwable.getMessage());
    }

    //可以在Controller中调用该方法进行websocket的手动发送以及参数调整
    public void sendMessage(String word) {
        connectToServer("ws://127.0.0.1:5580/ws");
        JsonObject frame = new JsonObject();
        //根据自己的需求填充你的请求参数
        //...
        webSocket.send(frame.toString());
        System.out.println(frame.toString());
    }
}
