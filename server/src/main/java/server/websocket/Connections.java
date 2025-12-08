package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void connect(Session session) {
        connections.put(session, session);
    }

    public void close(Session session) {
        connections.remove(session);
    }

    public void sendNotification(Session session, Notification message) throws IOException {
        String msg = message.getMessage();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(session)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void sendError(Session session, Error message) throws IOException {
        String msg = message.getMessage();
        if (connections.contains(session) && session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }

    public void sendGame(Session session, LoadGame game) throws IOException {
        String msg = game.getMessage();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                c.getRemote().sendString(msg);
            }
        }
    }
}
