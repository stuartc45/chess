package server.websocket;

import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
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

    public void sendNotification(WsMessageContext ctx, Integer gameID, Notification message) throws IOException {
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

    public void sendError(WsMessageContext ctx, Error message) throws IOException {
        String msg = message.getJson();
        if (connections.containsKey(ctx) && ctx.session.isOpen()) {
            ctx.send(msg);
        }
    }

    public void sendGame(WsMessageContext ctx, Integer gameID, LoadGame message) throws IOException {
        String msg = message.getJson();
        for (var c : connections.entrySet()) {
            if (c.getValue().equals(gameID)) {
                if (c.getKey().session.isOpen()) {
                    c.getKey().send(msg);
                }
            }
        }
    }
}
