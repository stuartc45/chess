package server.websocket;

import io.javalin.websocket.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    public final ConcurrentHashMap<WsMessageContext, Integer> connections = new ConcurrentHashMap<>();

    public void connect(Integer gameID, WsMessageContext ctx) {
        connections.put(ctx, gameID);
    }

    public void close(WsMessageContext ctx) {
        connections.remove(ctx);
    }

    public void sendNotification(WsMessageContext ctx, Integer gameID, NotificationMessage message) throws IOException {
        String msg = message.getJson();
        for (var c : connections.entrySet()) {
            if (c.getValue().equals(gameID)) {
                if (c.getKey().session.isOpen()) {
                    if (!c.getKey().equals(ctx)) {
                        c.getKey().send(msg);
                    }
                }
            }
        }
    }

    public void sendNotificationAll(Integer gameID, NotificationMessage message) {
        String msg = message.getJson();
        for (var c : connections.entrySet()) {
            if (c.getValue().equals(gameID)) {
                if (c.getKey().session.isOpen()) {
                    c.getKey().send(msg);
                }
            }
        }
    }

    public void sendError(WsMessageContext ctx, ErrorMessage message) {
        String msg = message.getJson();
        System.out.println(msg);
        if (connections.containsKey(ctx) && ctx.session.isOpen()) {
            ctx.send(msg);
        }
    }

    public void sendGame(WsMessageContext ctx, Integer gameID, LoadGameMessage message) {
        String newMessage = message.getJson();
        for (var session : connections.entrySet()) {
            if (session.getValue().equals(gameID)) {
                if (session.getKey().session.isOpen()) {
                    session.getKey().send(newMessage);
                }
            }
        }
    }
}
