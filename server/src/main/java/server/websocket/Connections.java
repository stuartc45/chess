package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void connect(Integer gameID, Session session) {
        connections.put(session, gameID);
    }

    public void close(Session session) {
        connections.remove(session);
    }

    public void sendNotification(Session session, Integer gameID, Notification message) throws IOException {
        String msg = message.getJson();
        for (var c : connections.entrySet()) {
            if (c.getValue().equals(gameID)) {
                if (c.getKey().isOpen()) {
                    if (!c.getKey().equals(session)) {
                        c.getKey().getRemote().sendString(msg);
                    }
                }
            }
        }
    }

    public void sendError(Session session, Error message) throws IOException {
        String msg = message.getJson();
        if (connections.contains(session) && session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }

    public void sendGame(Session session, Integer gameID, LoadGame message) throws IOException {
        String msg = message.getJson();
        for (var c : connections.entrySet()) {
            if (c.getValue().equals(gameID)) {
                if (c.getKey().isOpen()) {
                    c.getKey().getRemote().sendString(msg);
                }
            }
        }
    }
}
