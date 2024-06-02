package com.async.digitkingdom.common.utils;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketServer {
    // 客户端会话列表
    private static final Map<String, Session> clientSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onServerOpen(Session session) {
        // 客户端连接到本地 WebSocket 服务
        System.out.println("Client connected: " + session.getId());
        clientSessions.put(session.getId(), session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        // 处理客户端发送的消息
        System.out.println("Received message from client " + session.getId() + ": " + message);

        // 示例：将收到的消息广播给所有客户端
        //broadcast(message);
    }

    @OnClose
    public void onServerClose(Session session, CloseReason reason) {
        // 客户端断开连接
        System.out.println("Client " + session.getId() + " disconnected: " + reason);
        clientSessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // 客户端连接发生错误
        System.out.println("WebSocket client error: " + throwable.getMessage());
        clientSessions.remove(session.getId());
    }

    // 发送消息给指定客户端
    public void sendToClient(String clientId, String message) {
        Session session = clientSessions.get(clientId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    // 广播消息给所有客户端
    public void broadcast(String message) {
        for (Session session : clientSessions.values()) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        }
    }

    // 关闭客户端连接
    public void closeClientConnection(String clientId) {
        Session session = clientSessions.get(clientId);
        if (session != null && session.isOpen()) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Closing connection"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
